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

@Slf4j
@Service
public class DeviceDataListener {
    private static final String topic = "/lwm2m/data";

    @Autowired
    private Platform platform;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private ApplicationPropertiesConfig applicationPropertiesConfig;

    private MqttServiceApi mqttServiceApi;
    private Subscriber subscriber;

    private static MicroserviceCredentials microserviceCredentials;
    private static boolean isMicroserviceSubscribed = true;

    private final MqttMessageDeserializer mqttMessageDeserializer = new MqttMessageDeserializer();

    @EventListener(MicroserviceSubscriptionAddedEvent.class)
    public void onMicroserviceSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) {
        this.microserviceCredentials = event.getCredentials();
        log.info("This mircoservice is subscribed for tenant {}",microserviceCredentials.getTenant());
        createListener(microserviceCredentials);
    }

    @EventListener(MicroserviceSubscriptionsInitializedEvent.class)
    public void onMicroserviceSubscriptionInit() {
        log.info("This mircoservice is initialized for tenant {}",microserviceCredentials.getTenant());
        this.isMicroserviceSubscribed = true;
    }

    @EventListener(MicroserviceSubscriptionRemovedEvent.class)
    public void MicroserviceSubscriptionRemoved(MicroserviceSubscriptionRemovedEvent event) {
        if (!isMicroserviceSubscribed) {
            return;
        }

        this.microserviceCredentials = null;
        log.debug("This mircoservice is unsubscribed for tenant {}", event.getTenant());
    }


    private void setupPlatform(MicroserviceCredentials microserviceCredentials) {
        log.info("setting up platform");
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
        log.info("setting up service api");
        mqttServiceApi = MqttServiceApi.webSocket()
                .url(applicationPropertiesConfig.getProperties().getMessagingServiceUrl())
                .tokenApi(platform.getTokenApi())
                .build();

        // Build SubscriberConfig with topic and subscriber name
        final SubscriberConfig config = SubscriberConfig.subscriberConfig()
                .topic(topic)
                .subscriber(microserviceCredentials.getTenant() + "Subscriber")
                .build();

        log.info("building subscriber");
        // Build Subscriber
        subscriber = mqttServiceApi.buildSubscriber(config);

        log.info("creating the listener");
        // Subscribe by passing implementation of MessageListener to handle messages from the MQTT Service.
        subscriber.subscribe(new MessageListener() {
            @Override
            public void onMessage(MqttServiceMessage mqttServiceMessage) {
                log.info("received message");
                try {
                    Data lwm2mData = mqttMessageDeserializer.deserialize(mqttServiceMessage.getPayload(), Data.class);
                    log.info("Deserialized deviceData: {}", mqttMessageDeserializer.writeAsPrettyString(lwm2mData));
                } catch (IOException e) {
                    log.error("failed to read the device payload", e);
                }
            }
        });
        log.info("setup done");
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
