package com.transferwise.openbanking.client.error;

/**
 * Indicates that a HTTP call to an ASPSP failed, the request could not be successfully sent or the ASPSP did not
 * respond in the expected way.
 */
public class ApiCallException extends ClientException {

    public ApiCallException(String message) {
        super(message);
    }

    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
