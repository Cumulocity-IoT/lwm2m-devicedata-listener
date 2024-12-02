package com.cumulocity.example.lwm2m.DeviceDataListener.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MqttMessageDeserializer {
    private final ObjectMapper objectMapper;

    public MqttMessageDeserializer() {
        // Initialize Jackson's ObjectMapper
        this.objectMapper = new ObjectMapper();
    }

    public <T> T deserialize(byte[] data, Class<T> targetType) throws IOException {
        return objectMapper.readValue(data, targetType);
    }
}
