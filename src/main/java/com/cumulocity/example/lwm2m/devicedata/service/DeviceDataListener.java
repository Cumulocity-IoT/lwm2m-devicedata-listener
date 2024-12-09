package com.cumulocity.example.lwm2m.devicedata.service;

import com.cumulocity.example.lwm2m.devicedata.config.ApplicationPropertiesConfig;
import com.cumulocity.example.lwm2m.devicedata.deserializer.MqttMessageDeserializer;
import com.cumulocity.example.lwm2m.devicedata.lwm2m.Data;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionsInitializedEvent;
import com.cumulocity.model.authentication.CumulocityCredentialsFactory;
import com.cumulocity.mqtt.service.sdk.MqttServiceApi;
import com.cumulocity.mqtt.service.sdk.listener.MessageListener;
import com.cumulocity.mqtt.service.sdk.model.MqttServiceMessage;
import com.cumulocity.mqtt.service.sdk.subscriber.Subscriber;
import com.cumulocity.mqtt.service.sdk.subscriber.SubscriberConfig;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformBuilder;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class DeviceDataListener {
    private final MqttMessageDeserializer mqttMessageDeserializer = new MqttMessageDeserializer();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private Platform platform;

    @Autowired
    private ApplicationPropertiesConfig applicationPropertiesConfig;

    private MqttServiceApi mqttServiceApi;
    private Subscriber subscriber;

    private void setupPlatform(MicroserviceCredentials microserviceCredentials) {
        log.info("Setting up Platform authentication");
        platform = PlatformBuilder.platform()
                .withBaseUrl(applicationPropertiesConfig.getProperties().getBaseUrl())
                .withCredentials(new CumulocityCredentialsFactory()
                        .withTenant(microserviceCredentials.getTenant())
                        .withUsername(microserviceCredentials.getUsername())
                        .withPassword(microserviceCredentials.getPassword())
                        .getCredentials())
                .build();
    }

    public void createListener(MicroserviceCredentials microserviceCredentials) {
        setupPlatform(microserviceCredentials);
        log.info("Setting up MQTT Messaging Service API");
        mqttServiceApi = MqttServiceApi.webSocket()
                .url(applicationPropertiesConfig.getProperties().getMessagingServiceUrl())
                .tokenApi(platform.getTokenApi())
                .build();

        // Build SubscriberConfig with topic and subscriber name
        final SubscriberConfig config = SubscriberConfig.subscriberConfig()
                .topic(applicationPropertiesConfig.getProperties().getMessagingServiceTopic())
                .subscriber(microserviceCredentials.getTenant() + "Subscriber")
                .build();

        log.info("Building MQTT Messaging Service API subscriber");
        // Build Subscriber
        subscriber = mqttServiceApi.buildSubscriber(config);

        log.info("Creating MQTT Messaging Service listener");
        // Subscribe by passing implementation of MessageListener to handle messages from the MQTT Service.
        subscriber.subscribe(new MessageListener() {
            @Override
            public void onMessage(MqttServiceMessage mqttServiceMessage) {
                log.info("Received MQTT Messaging Service message");
                try {
                    Data lwm2mData = mqttMessageDeserializer.deserialize(mqttServiceMessage.getPayload(), Data.class);
                    log.info("Deserialized deviceData: {}", mqttMessageDeserializer.writeAsPrettyString(lwm2mData));
                } catch (IOException e) {
                    log.error("Failed to read the MQTT Messaging Service device payload", e);
                }
            }
        });
        log.info("Setting up MQTT Messaging Service API: Done");
    }

    @Scheduled(fixedDelay = 5000)
    public void heartbeat() {
        log.info("heartbeat check");
    }

    @PreDestroy
    public void shutdown() {
        // Close the resources after usage
        subscriber.close();
        mqttServiceApi.close();
    }
}

