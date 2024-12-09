package com.cumulocity.example.lwm2m.devicedata.service;

import com.cumulocity.example.lwm2m.devicedata.config.ApplicationPropertiesConfig;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionsInitializedEvent;
import com.cumulocity.mqtt.service.sdk.MqttServiceApi;
import com.cumulocity.mqtt.service.sdk.subscriber.Subscriber;
import com.cumulocity.sdk.client.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class C8yMicroserviceEventListener {
    private static MicroserviceCredentials microserviceCredentials;
    private static boolean isMicroserviceSubscribed = true;

    @Autowired
    private Platform platform;

    @Autowired
    private ApplicationPropertiesConfig applicationPropertiesConfig;

    @Autowired
    DeviceDataListener deviceDataListener;

    private MqttServiceApi mqttServiceApi;
    private Subscriber subscriber;

    @EventListener(MicroserviceSubscriptionAddedEvent.class)
    public void onMicroserviceSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) {
        microserviceCredentials = event.getCredentials();
        log.info("This microservice is subscribed for tenant {}",microserviceCredentials.getTenant());
        deviceDataListener.createListener(microserviceCredentials);
    }

    @EventListener(MicroserviceSubscriptionsInitializedEvent.class)
    public void onMicroserviceSubscriptionInit() {
        log.info("This microservice is initialized for tenant {}",microserviceCredentials.getTenant());
        isMicroserviceSubscribed = true;
    }

    @EventListener(MicroserviceSubscriptionRemovedEvent.class)
    public void MicroserviceSubscriptionRemoved(MicroserviceSubscriptionRemovedEvent event) {
        if (!isMicroserviceSubscribed) {
            return;
        }

        // When the service is unsubscribed, then it's better to shutdown also the listener
        deviceDataListener.shutdown();
        microserviceCredentials = null;
        log.debug("This microservice is unsubscribed for tenant {}", event.getTenant());
    }
}
