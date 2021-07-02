package com.transferwise.openbanking.client.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Implementation of the {@link JsonConverter} interface, using the Jackson library for JSON operations.
 * <p>
 * Instances of this class are thread safe and can be re-used to to avoid un-necessarily creating multiple instances of
 * the heavy {@link ObjectMapper} class.
 */
public class JacksonJsonConverter implements JsonConverter {

    private final ObjectMapper objectMapper;

    public JacksonJsonConverter() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    public String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonWriteException("Unable to write object to JSON string", e, value);
        }
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new JsonReadException("Unable to read JSON string to " + valueType.getSimpleName() + " object",
                e,
                content);
        }
    }
}
