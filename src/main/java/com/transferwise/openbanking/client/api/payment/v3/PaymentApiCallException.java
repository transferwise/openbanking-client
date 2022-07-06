package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBErrorResponse1;
import com.transferwise.openbanking.client.error.ApiCallException;
import lombok.Getter;

/**
 * Indicates that a HTTP call to an ASPSP failed, the request could not be successfully sent or the ASPSP did not
 * respond in the expected way.
 */
@Getter
public class PaymentApiCallException extends ApiCallException {

    /**
     * Open Banking error response.
     */
    private final OBErrorResponse1 errorResponse;

    public PaymentApiCallException(String message) {
        this(message, null, null);
    }

    public PaymentApiCallException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public PaymentApiCallException(String message, Throwable cause, OBErrorResponse1 errorResponse) {
        super(message, cause);
        this.errorResponse = errorResponse;
    }
}
