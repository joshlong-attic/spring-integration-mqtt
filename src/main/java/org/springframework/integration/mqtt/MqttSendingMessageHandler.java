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
import org.springframework.integration.*;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.util.Assert;

/**
 * Spring Integration {@link AbstractMessageHandler message handler } that delivers Spring Integration messages
 *
 * @author Josh Long
 * @author Andy Piper
 */
public class MqttSendingMessageHandler extends AbstractMessageHandler {
    private IMqttClient client;
    private String topic;
    private boolean messagesRetained;
    private QualityOfService qualityOfService = QualityOfService.AT_MOST_ONCE;

    @Override
    protected void onInit() throws Exception {
        Assert.notNull(this.client, String.format("you must specify a valid %s instance! ", MqttClient.class.getName()));
        Assert.hasText(this.topic, "you must specify a 'topic'");
        Assert.notNull(this.qualityOfService, String.format("you must specify a non-null instance of the %s enum.", QualityOfService.class.getName()));
    }

    public void setClient(IMqttClient client) {
        this.client = client;
    }

    public void setQualityOfService(QualityOfService qualityOfService) {
        this.qualityOfService = qualityOfService;
    }

    public void setMessagesRetained(boolean messagesRetained) {
        this.messagesRetained = messagesRetained;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    protected void handleMessageInternal(Message<?> message) throws Exception {
        Object payload = message.getPayload();
        Assert.isTrue(payload instanceof byte[], String.format("the payload for %s must be of type byte[]", getClass().getName()));
        byte[] payloadOfBytes = (byte[]) payload;

        MessageHeaders messageHeaders = message.getHeaders();

        // todo is this a thing?
        String topicForThisMessage = this.topic;
        if (messageHeaders.containsKey(MqttHeaders.TOPIC)) {
            topicForThisMessage = (String) messageHeaders.get(MqttHeaders.TOPIC);
        }

        // todo should we support mapping other things like qos? messagesRetained?

        this.client.publish(topicForThisMessage, payloadOfBytes, this.qualityOfService.ordinal(), this.messagesRetained);
    }
}