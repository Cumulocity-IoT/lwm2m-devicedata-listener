package com.cumulocity.example.lwm2m.DeviceDataListener.service;

import com.cumulocity.example.lwm2m.DeviceDataListener.config.ConfigProperties;
import com.cumulocity.example.lwm2m.DeviceDataListener.deserializer.MqttMessageDeserializer;
import com.cumulocity.example.lwm2m.DeviceDataListener.lwm2m.Data;
import com.cumulocity.model.authentication.CumulocityCredentialsFactory;
import com.cumulocity.mqtt.service.sdk.MqttServiceApi;
import com.cumulocity.mqtt.service.sdk.listener.MessageListener;
import com.cumulocity.mqtt.service.sdk.model.MqttServiceMessage;
import com.cumulocity.mqtt.service.sdk.subscriber.Subscriber;
import com.cumulocity.mqtt.service.sdk.subscriber.SubscriberConfig;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class DeviceDataListener {
    private static final String topic = "lwm2m/data";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ConfigProperties configProperties;

    private MqttServiceApi mqttServiceApi;
    private Subscriber subscriber;

    private final Platform platform;
    private final MqttMessageDeserializer mqttMessageDeserializer;

    @Autowired
    public DeviceDataListener(ConfigProperties configProperties) {
        this.configProperties = configProperties;

        mqttMessageDeserializer = new MqttMessageDeserializer();

        log.info("setting up platform");
        platform = PlatformBuilder.platform()
                .withBaseUrl(configProperties.getBaseUrl())
                .withCredentials(new CumulocityCredentialsFactory().withTenant(configProperties.getTenant())
                        .withUsername(configProperties.getUsername())
                        .withPassword(configProperties.getPassword())
                        .getCredentials())
                .build();
    }

    @PostConstruct
    public void createListener() {
        log.info("setting up service api");
        mqttServiceApi = MqttServiceApi.webSocket()
                .url(configProperties.getMessagingServiceUrl())
                .tokenApi(platform.getTokenApi())
                .build();

        // Build SubscriberConfig with topic and subscriber name
        final SubscriberConfig config = SubscriberConfig.subscriberConfig()
                .topic(topic)
                .subscriber(configProperties.getTenant() + "Subscriber")
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
                    log.info("Deserialized deviceData: {}", lwm2mData);
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
