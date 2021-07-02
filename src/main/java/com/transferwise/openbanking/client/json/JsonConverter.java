package com.transferwise.openbanking.client.json;

/**
 * An interface specifying the operations for serializing and de-serializing JSON.
 */
public interface JsonConverter {

    /**
     * Serialize the given object as a JSON string.
     *
     * @param value The object to serialize
     * @return The serialized JSON string
     * @throws JsonWriteException if there was a problem writing the object to JSON
     */
    String writeValueAsString(Object value);

    /**
     * De-serialize the given JSON string to a Java object.
     *
     * @param content The JSON string to de-serialize
     * @param valueType The class type to de-serialize to
     * @param <T> The type to de-serialize to
     * @return The de-serialized JSON object
     * @throws JsonReadException if there was a problem reading the JSON to an object
     */
    <T> T readValue(String content, Class<T> valueType);
}
