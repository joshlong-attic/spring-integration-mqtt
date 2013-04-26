package org.springframework.integration.mqtt;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.context.annotation.*;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

public class Main {

    public static void main(String[] args) throws Throwable {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MqttConfiguration.class);

        MessageChannel messageChannel = context.getBean("messages", MessageChannel.class);
        messageChannel.send(MessageBuilder.withPayload("Josh and Andy say hi!".getBytes()).build());
    }

    @Configuration
    @ImportResource("simple-integration.xml")
    public static class MqttConfiguration {
        @Bean
        public MqttClientFactoryBean mqttClientFactoryBean() {
            return new MqttClientFactoryBean("m2m.eclipse.org");
        }

        @Bean
        public MqttSendingMessageHandler mqttSendingMessageHandler(IMqttClient client) {
            return new MqttSendingMessageHandler(client, "cats");
        }
    }
}
