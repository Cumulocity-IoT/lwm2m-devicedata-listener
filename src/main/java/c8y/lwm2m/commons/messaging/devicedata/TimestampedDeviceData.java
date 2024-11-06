package c8y.lwm2m.commons.messaging.devicedata;

import lombok.Getter;
import org.joda.time.Instant;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TimestampedDeviceData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567890L;

    private final String tenant;
    private final Integer deviceId;
    private final String deviceName;
    private final Long timestamp = System.currentTimeMillis();

    private final Map<Instant, Map<SerializableLwM2mPath, LwM2mResource>> timestampedDeviceDataMap = new HashMap<>();

    public TimestampedDeviceData(String tenant, Integer deviceId, String deviceName) {
        this.tenant = tenant;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }


    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n------------------------------------------------\n");
        stringBuffer.append("tenant: ").append(tenant).append("\n");
        stringBuffer.append("device id: ").append(tenant).append("\n");
        stringBuffer.append("device name:").append(tenant).append("\n");
        stringBuffer.append("timestamp creation:").append(timestamp).append("\n");
        timestampedDeviceDataMap.forEach((k,v) -> {
            stringBuffer.append(k.toString()).append("\n");
            v.forEach((path, value) -> {
                stringBuffer.append("\t").append(path.toString());
                if (value.isMultipleInstance() || value instanceof LwM2mMultipleResourceInstance) {
                    LwM2mMultipleResourceInstance m2mMultipleResourceInstance = (LwM2mMultipleResourceInstance) value;
                    m2mMultipleResourceInstance.instances.forEach((id, val) -> {
                        stringBuffer.append("\n\t\t").append(val.resourceId).append(":").append(val.getResourceValue()).append(":").append(val.getResourceType());
                    });
                } else {
                    LwM2mResourceInstance resourceInstance = (LwM2mResourceInstance) value;
                    stringBuffer.append(" ").append(resourceInstance.getResourceValue()).append(":").append(resourceInstance.getResourceType());
                }
                stringBuffer.append("\n");
            });
        });
        stringBuffer.append("------------------------------------------------\n");

        return stringBuffer.toString();
    }

}
