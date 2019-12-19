package com.transferwise.openbanking.client.api.payment.v1;

import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;

/**
 * An interface specifying operations for generating idempotency keys for the version 1 payment requests.
 */
public interface IdempotencyKeyGenerator {

    /**
     * Generate the idempotency key for a request to setup a payment.
     *
     * @param setupPaymentRequest The details of the payment to setup
     * @return The generated idempotency key
     */
    String generateIdempotencyKey(SetupPaymentRequest setupPaymentRequest);

    /**
     * Generate an idempotency key for a request to submit a payment for execution.
     *
     * @param submitPaymentRequest The details of the payment to submit for execution
     * @return The generated idempotency key
     */
    String generateIdempotencyKey(SubmitPaymentRequest submitPaymentRequest);
}
