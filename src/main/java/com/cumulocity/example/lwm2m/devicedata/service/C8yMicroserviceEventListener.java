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

    /**
     * Listens for the {@link MicroserviceSubscriptionAddedEvent} event to indicate that a microservice
     * has been successfully registered. Once this event is detected, the listener can be started
     * to begin processing related tasks or actions.
     *
     * This listener ensures that the microservice subscription process is completed before
     * initiating any dependent actions.
     * @param event
     */
    @EventListener(MicroserviceSubscriptionAddedEvent.class)
    public void onMicroserviceSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) {
        microserviceCredentials = event.getCredentials();
        log.info("This microservice is subscribed for tenant {}",microserviceCredentials.getTenant());
        deviceDataListener.init(microserviceCredentials);
    }

    /**
     * Listens for the {@link MicroserviceSubscriptionsInitializedEvent} event, which indicates that
     * the microservice has been fully initialized and is now ready to operate. Once this event is triggered,
     * the microservice is fully running and can begin handling MQTT messages for the topic it was subscribed to.
     *
     * This event ensures that all necessary initialization steps are completed before the microservice
     * starts processing incoming messages.
     */
    @EventListener(MicroserviceSubscriptionsInitializedEvent.class)
    public void onMicroserviceSubscriptionInit() {
        log.info("This microservice is initialized for tenant {}",microserviceCredentials.getTenant());
        isMicroserviceSubscribed = true;
    }

    /**
     * Listens for the {@link MicroserviceSubscriptionRemovedEvent} event, which indicates that
     * the microservice has been unsubscribed. Upon receiving this event, for security reasons,
     * the previously established session with the platform is closed, and the MQTT messaging service
     * API setup is cleared to ensure that no further interactions occur.
     *
     * This action helps maintain security and ensures that any resources tied to the unsubscribed
     * microservice are properly cleaned up.
     * @param event
     */
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
