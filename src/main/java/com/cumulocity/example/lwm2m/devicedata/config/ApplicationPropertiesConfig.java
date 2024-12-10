package com.cumulocity.example.lwm2m.devicedata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationPropertiesConfig {
    @Value("${application.name:lwm2m-devicedata-listener}")
    private String applicationName;

    @Value("${C8Y.baseURL}")
    private String baseUrl;

    @Value("${C8Y.mqtt.protocol}")
    private String messagingServiceProtocol;

    @Value("${C8Y.mqtt.topic}")
    private String messagingServiceTopic;

    @Bean
    public ApplicationProperties getProperties() {
        return new ApplicationProperties(applicationName, baseUrl, messagingServiceProtocol, messagingServiceTopic);
    }
}
