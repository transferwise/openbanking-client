package com.transferwise.openbanking.client.api.common;

/**
 * An interface specifying operations for generating idempotency keys for requests.
 *
 * @param <SETUP_REQUEST>      The setup request type
 * @param <SUBMISSION_REQUEST> The submit request type
 */
public interface IdempotencyKeyGenerator<SETUP_REQUEST, SUBMISSION_REQUEST> {

    /**
     * Generate the idempotency key for a setup request.
     *
     * @param setupRequest The details of a request to setup
     * @return The generated idempotency key
     */
    String generateKeyForSetup(SETUP_REQUEST setupRequest);

    /**
     * Generate an idempotency key for a submit request.
     *
     * @param submitRequest The details of a request to submit
     * @return The generated idempotency key
     */
    String generateKeyForSubmission(SUBMISSION_REQUEST submitRequest);
}
