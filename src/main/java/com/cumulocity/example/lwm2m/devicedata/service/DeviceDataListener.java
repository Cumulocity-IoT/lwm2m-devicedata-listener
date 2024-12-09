package com.cumulocity.example.lwm2m.devicedata.service;

import com.cumulocity.example.lwm2m.devicedata.config.ApplicationPropertiesConfig;
import com.cumulocity.example.lwm2m.devicedata.deserializer.MqttMessageDeserializer;
import com.cumulocity.example.lwm2m.devicedata.lwm2m.Data;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
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

    /**
     * Authenticates against the Platform using the {@link MicroserviceCredentials}
     * obtained during the microservice initialization process. These credentials
     * are used to establish a secure connection and ensure that the microservice
     * can interact with the platform in a trusted manner.
     * @param microserviceCredentials
     */
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

    /**
     * Sets up the MQTT Messaging service API, initializing the necessary configurations
     * and establishing the required connections to enable the service to send and
     * receive MQTT messages. This setup ensures that the microservice can communicate
     * effectively over MQTT and interact with the subscribed topics.
     */
    private void setupMqttMessagingServiceAPI() {
        mqttServiceApi = MqttServiceApi.webSocket()
                .url(applicationPropertiesConfig.getProperties().getMessagingServiceUrl())
                .tokenApi(platform.getTokenApi())
                .build();
    }

    /**
     * Subscribes the given {@param tenant} to the specified {@param topic}.
     * This action ensures that the tenant will receive messages published
     * to the topic and can interact with the related data or events.
     * @param tenant The tenant to be subscribed to the topic.
     * @param topic The topic to which the tenant should be subscribed.
     */
    private void subcribeTenantForTopic(String tenant, String topic) {
        // Subscriber ID must be a unique name and should not overlap with another subscriber with the same ID
        final String subscriberID = tenant + "LwM2mSubscriber";

        log.info("Configure MQTT Messaging Service subscriber for tenant '{}' to MQTT topic '{}' with Subscriber ID '{}'",
                tenant, topic, subscriberID);
        // Build SubscriberConfig with topic and subscriber name
        final SubscriberConfig config = SubscriberConfig.subscriberConfig()
                .topic(topic)
                .subscriber(subscriberID)
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
    }

    /**
     * Creates an MQTT Messaging Service listener to subscribe to the specified
     * 'C8Y.mqtt.topic'. This listener will enable the service to receive and
     * process MQTT messages published to the topic, facilitating communication
     * with the platform or other services using MQTT.
     * @param microserviceCredentials The credentials of the tenant and user
     *                                who have deployed the microservice. These
     *                                credentials are used for authentication
     *                                and authorization when interacting with the
     *                                platform or services.
     */
    public void init(MicroserviceCredentials microserviceCredentials) {
        setupPlatform(microserviceCredentials);

        log.info("Setting up MQTT Messaging Service API");
        setupMqttMessagingServiceAPI();

        subcribeTenantForTopic(microserviceCredentials.getTenant(),
                applicationPropertiesConfig.getProperties().getMessagingServiceTopic());
        log.info("Setting up MQTT Messaging Service API: Done");
    }

    @Scheduled(fixedDelay = 5000)
    public void heartbeat() {
        log.info("heartbeat check");
    }

    /**
     * Closes the platform session that was created during initialization and cleans up
     * the MQTT service API setup. This ensures that any resources allocated during the
     * initialization process are properly released, maintaining system stability and
     * preventing any unwanted side effects.
     */
    @PreDestroy
    public void shutdown() {
        // Close the resources after usage
        subscriber.close();
        mqttServiceApi.close();
    }
}

