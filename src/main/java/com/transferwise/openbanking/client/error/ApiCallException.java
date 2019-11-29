package com.transferwise.openbanking.client.error;

public class ApiCallException extends ClientException {

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
