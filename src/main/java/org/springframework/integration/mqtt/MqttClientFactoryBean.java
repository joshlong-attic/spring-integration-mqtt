/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.*;
import org.springframework.util.*;

/**
 * Creates {@link MqttClient client} to be used when connecting
 * to a broker (such as the one at <CODE>m2m.eclipse.org</CODE>)
 * to publish and consume messages.
 *
 * @author Andy Piper
 * @author Josh Long
 */
@SuppressWarnings("unused")
public class MqttClientFactoryBean implements FactoryBean<IMqttClient>, InitializingBean {

    private static String TCP_PROTOCOL = "tcp://";
    private static String SSL_PROTOCOL = "ssl://";
    private String protocol = TCP_PROTOCOL;
    private boolean useSsl = false;
    private String host;
    private int port = 1883;
    private String clientId = buildClientId();
    private MqttClientPersistence mqttClientPersistence;
    private String username, password;
    private MqttConnectOptions mqttConnectOptions;
    private Boolean cleanSession = null;

    public MqttClientFactoryBean(String host) {
        setup(host, this.username, this.password);
    }

    public MqttClientFactoryBean(String host, String u, String p) {
        setup(host, u, p);
    }

    public MqttClientFactoryBean(String host, int port, String u, String p) {
        setup(host, u, p);
        this.setPort(port);
    }

    protected void setup(String h, String u, String p) {
        setHost(h);
        setUsername(u);
        setPassword(p);
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public void setMqttConnectOptions(MqttConnectOptions mqttConnectOptions) {
        this.mqttConnectOptions = mqttConnectOptions;
    }

    public void setClientId(String c) {
        this.clientId = c;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMqttClientPersistence(MqttClientPersistence mqttClientPersistence) {
        this.mqttClientPersistence = mqttClientPersistence;
    }

    @Override
    public IMqttClient getObject() throws Exception {
        String serverUri = buildServerUri();
        MqttClient client = this.mqttClientPersistence == null ?
                new MqttClient(serverUri, clientId) :
                new MqttClient(serverUri, clientId, mqttClientPersistence);
        MqttConnectOptions connectOptions = this.buildMqttConnectionOptions();
        if (null != connectOptions) {
            client.connect(connectOptions);
        } else {
            client.connect();
        }
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return IMqttClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.protocol, String.format("you must specify a non-null protocol value (either %s or %s)", SSL_PROTOCOL, TCP_PROTOCOL));
        Assert.isTrue(this.protocol.equalsIgnoreCase(SSL_PROTOCOL) || this.protocol.equalsIgnoreCase(TCP_PROTOCOL), "");
        Assert.hasText(this.clientId, "your clientId must be non-null");
        Assert.hasText(this.host, "you must specify a valid host");
        Assert.isTrue(this.port > 0, "you must specify a valid port");
        boolean connectionOptionsAreCorrectlySpecified =
                this.mqttConnectOptions != null && weShouldCreateConnectionOptions();
        Assert.isTrue(!connectionOptionsAreCorrectlySpecified,
                String.format("you must specify an instance of %s for the 'buildMqttConnectionOptions' attribute" +
                        " OR any of the following options ('cleanSession', 'username', 'password'), but not both!", MqttConnectOptions.class.getName()));

    }

    protected String buildServerUri() {
        if (this.useSsl) {
            this.protocol = SSL_PROTOCOL;
        }
        return this.protocol + this.host + ":" + this.port;
    }

    protected boolean weShouldCreateConnectionOptions() {
        return (this.cleanSession != null || StringUtils.hasText(this.username) || StringUtils.hasText(this.password));
    }

    protected String buildClientId() {
        String user = System.getProperty("user.name");
        int totalLength = 23;
        int userLength = user.length();
        if (userLength > 10) {
            user = user.substring(0, 10);
        }

        String clientId = user + System.currentTimeMillis();
        Assert.isTrue(clientId.length() <= totalLength);
        return clientId;
    }

    protected MqttConnectOptions buildMqttConnectionOptions() {
        MqttConnectOptions connectOptions = null;
        if (weShouldCreateConnectionOptions()) {
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(this.cleanSession);
            connectOptions.setUserName(this.username);
            connectOptions.setPassword(this.password.toCharArray());
        } else if (this.mqttConnectOptions != null) {
            connectOptions = this.mqttConnectOptions;
        }
        return connectOptions;
    }
}


