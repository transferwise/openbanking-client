package com.transferwise.openbanking.client.api.event;

import com.transferwise.openbanking.client.api.payment.v3.model.event.OBErrorResponse1;
import com.transferwise.openbanking.client.error.ApiCallException;
import lombok.Getter;

@Getter
public class EventApiCallException extends ApiCallException {

    private final OBErrorResponse1 errorResponse;

    public EventApiCallException(String message) {
        this(message, null, null);
    }

    public EventApiCallException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public EventApiCallException(String message, Throwable cause, OBErrorResponse1 errorResponse) {
        super(message, cause);
        this.errorResponse = errorResponse;
    }
}
