package com.cumulocity.example.lwm2m.devicedata.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
//@AllArgsConstructor
@Slf4j
public class ApplicationProperties {
    private String applicationName;
    private String baseUrl;
    private String messagingServiceUrl;

    public ApplicationProperties(String applicationName, String baseUrl, String messagingServiceUrl) {
        log.info("constructor ===================================");
        this.applicationName = applicationName;
        this.baseUrl = baseUrl;
        this.messagingServiceUrl = messagingServiceUrl;
    }

    @PostConstruct
    public void printGatewayInformation() {
        log.info("Platform URL: {}", baseUrl);
        log.info("Messaging Service URL: {}", messagingServiceUrl);
    }
}