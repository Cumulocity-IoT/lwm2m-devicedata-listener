package c8y.lwm2m.commons.messaging.devicedata;

import java.io.Serializable;

public interface LwM2mResource extends Serializable {
    Integer getResourceId();

    Object getResourceValue();

    ResourceType getResourceType();

    Boolean isMultipleInstance();
}
