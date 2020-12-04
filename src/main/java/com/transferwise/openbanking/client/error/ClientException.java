package com.transferwise.openbanking.client.error;

/**
 * Indicates that a problem was encountered within the client when trying to carry out an operation.
 */
public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
