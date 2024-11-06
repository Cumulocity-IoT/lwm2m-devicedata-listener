package c8y.lwm2m.commons.messaging.devicedata;

import java.io.Serializable;

public enum ResourceType implements Serializable {
    NONE, STRING, INTEGER, FLOAT, BOOLEAN, OPAQUE, TIME, OBJLNK, UNSIGNED_INTEGER, CORELINK;

    public boolean isNumeric() {
        switch (this) {
            case INTEGER:
            case FLOAT:
            case UNSIGNED_INTEGER:
                return true;
            default:
                return false;
        }
    }

}
