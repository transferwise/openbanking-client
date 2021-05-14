package com.transferwise.openbanking.client.json;

import com.transferwise.openbanking.client.error.ClientException;

/**
 * Indicates there was a problem reading a JSON string to a Java object.
 */
public class JsonReadException extends ClientException {

    /**
     * The JSON string that was attempted to be read.
     */
    private final String json;

    public JsonReadException(String message, String json) {
        super(message);
        this.json = json;
    }

    public JsonReadException(String message, Throwable cause, String json) {
        super(message, cause);
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
