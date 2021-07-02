package com.transferwise.openbanking.client.json;

import com.transferwise.openbanking.client.error.ClientException;

/**
 * Indicates there was a problem writing an objet to a JSON string.
 */
public class JsonWriteException extends ClientException {

    /**
     * The Java object that was attempted to be written.
     */
    private final Object value;

    public JsonWriteException(String message, Object value) {
        super(message);
        this.value = value;
    }

    public JsonWriteException(String message, Throwable cause, Object value) {
        super(message, cause);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
