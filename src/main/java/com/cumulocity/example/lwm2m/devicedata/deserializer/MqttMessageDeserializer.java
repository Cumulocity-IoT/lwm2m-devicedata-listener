package com.cumulocity.example.lwm2m.devicedata.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

public class MqttMessageDeserializer {
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;

    public MqttMessageDeserializer() {
        // Initialize Jackson's ObjectMapper
        this.objectMapper = new ObjectMapper();
        this.objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    public <T> T deserialize(byte[] data, Class<T> targetType) throws IOException {
        return objectMapper.readValue(data, targetType);
    }

    public String writeAsPrettyString(Object jsonObject) throws JsonProcessingException {
        return objectWriter.writeValueAsString(jsonObject);
    }
}
