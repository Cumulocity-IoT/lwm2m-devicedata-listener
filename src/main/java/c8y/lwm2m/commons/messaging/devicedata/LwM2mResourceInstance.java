package c8y.lwm2m.commons.messaging.devicedata;

import java.io.Serial;

public class LwM2mResourceInstance implements LwM2mResource {
    @Serial
    private static final long serialVersionUID = 1234567890L;

    Integer resourceId;
    Object value;
    ResourceType type;
    Boolean isMultipleInstance;

    public LwM2mResourceInstance(Integer resourceId, Object value, ResourceType type, Boolean isMultipleInstance) {
        this.resourceId = resourceId;
        this.value = value;
        this.type = type;
        this.isMultipleInstance = isMultipleInstance;
    }

    @Override
    public Integer getResourceId() {
        return resourceId;
    }

    @Override
    public Object getResourceValue() {
        return value;
    }

    @Override
    public ResourceType getResourceType() {
        return type;
    }

    @Override
    public Boolean isMultipleInstance() {
        return isMultipleInstance;
    }
}
