package c8y.lwm2m.commons.messaging.devicedata;

import java.io.Serial;
import java.util.Map;

public class LwM2mMultipleResourceInstance implements LwM2mResource {
    @Serial
    private static final long serialVersionUID = 1234567890L;

    Integer resourceId;
    Map<Integer, LwM2mResourceInstance> instances;
    ResourceType type;

    public LwM2mMultipleResourceInstance(Integer resourceId, Map<Integer, LwM2mResourceInstance> instances, ResourceType type) {
        this.resourceId = resourceId;
        this.instances = instances;
        this.type = type;
    }

    @Override
    public Integer getResourceId() {
        return resourceId;
    }

    @Override
    public Object getResourceValue() {
        return instances;
    }

    @Override
    public ResourceType getResourceType() {
        return type;
    }

    @Override
    public Boolean isMultipleInstance() {
        return false;
    }
}
