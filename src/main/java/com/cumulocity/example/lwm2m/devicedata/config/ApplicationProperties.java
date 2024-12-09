package com.cumulocity.example.lwm2m.devicedata.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class ApplicationProperties {
    private String applicationName;
    private String baseUrl;
    private String messagingServiceUrl;
    private String messagingServiceTopic;

    public ApplicationProperties(String applicationName, String baseUrl, String messagingServiceUrl) {
        this.applicationName = applicationName;
        this.baseUrl = baseUrl;
        this.messagingServiceUrl = messagingServiceUrl;
    }

    @PostConstruct
    public void printGatewayInformation() {
        log.info("============================== Application Properties ==================================");
        log.info("Platform URL: {}", baseUrl);
        log.info("Messaging Service URL: {}", messagingServiceUrl);
        log.info("Messaging Service Topic: {}", messagingServiceTopic);
        log.info("========================================================================================");
    }
}