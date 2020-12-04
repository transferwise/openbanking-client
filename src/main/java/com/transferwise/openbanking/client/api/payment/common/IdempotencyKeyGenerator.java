package com.transferwise.openbanking.client.api.payment.common;

/**
 * An interface specifying operations for generating idempotency keys for the payment requests, supporting all payments
 * API versions where there are separate payment creation and payment submission endpoints.
 *
 * @param <SETUP_REQUEST>      The setup payment request type
 * @param <SUBMISSION_REQUEST> The submit payment request type
 */
public interface IdempotencyKeyGenerator<SETUP_REQUEST, SUBMISSION_REQUEST> {

    /**
     * Generate the idempotency key for a request to setup a payment.
     *
     * @param setupPaymentRequest The details of the payment to setup
     * @return The generated idempotency key
     */
    String generateKeyForSetup(SETUP_REQUEST setupPaymentRequest);

    /**
     * Generate an idempotency key for a request to submit a payment for execution.
     *
     * @param submitPaymentRequest The details of the payment to submit for execution
     * @return The generated idempotency key
     */
    String generateKeyForSubmission(SUBMISSION_REQUEST submitPaymentRequest);
}
