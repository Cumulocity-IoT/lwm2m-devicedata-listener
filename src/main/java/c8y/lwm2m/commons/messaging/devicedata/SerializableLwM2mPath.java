package c8y.lwm2m.commons.messaging.devicedata;

import lombok.Getter;
import org.eclipse.leshan.core.node.LwM2mPath;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class SerializableLwM2mPath implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567890L;

    Integer objectId;
    Integer objectInstanceId;
    Integer resourceId;
    Integer resourceInstanceId;

    public SerializableLwM2mPath(LwM2mPath lwM2mPath) {
        this.objectId = lwM2mPath.getObjectId();
        this.objectInstanceId = lwM2mPath.getObjectInstanceId();
        this.resourceId = lwM2mPath.getResourceId();
        this.objectInstanceId = lwM2mPath.getResourceInstanceId();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("/");
        stringBuilder.append(objectId).append("/").append(objectInstanceId).append("/").append(resourceId);
        if (resourceInstanceId != null) {
            stringBuilder.append("/").append(resourceInstanceId);
        }
        return stringBuilder.toString();
    }
}
