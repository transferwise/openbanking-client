package com.transferwise.openbanking.client.api.payment.v1;

import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;
import com.transferwise.openbanking.client.aspsp.AspspDetails;

/**
 * An interface specifying the operations for a client supporting version 1 single immediate domestic payments.
 */
public interface PaymentClient {

    /**
     * Setup a new single immediate domestic payment, which can then be authorised and submitted for execution.
     *
     * @param setupPaymentRequest The details of the payment to setup
     * @param aspspDetails        The details of the ASPSP to send the request to
     * @return The result, from the ASPSP, of the payment setup request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    PaymentSetupResponse setupPayment(SetupPaymentRequest setupPaymentRequest, AspspDetails aspspDetails);

    /**
     * Submits a setup and approved immediate domestic payment for execution by the ASPSP.
     *
     * @param submitPaymentRequest The details of the payment to submit for execution
     * @param authorizationCode    The payment authorization code returned by the ASPSP, as a result of a successful
     *                             payment authorization by the account holder
     * @param aspspDetails         The details of the ASPSP to send the request to
     * @return The result, from the ASPSP, of the payment submission request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    PaymentSubmissionResponse submitPayment(SubmitPaymentRequest submitPaymentRequest,
                                            String authorizationCode,
                                            AspspDetails aspspDetails);

    /**
     * Get the details of a previously submitted payment.
     *
     * @param paymentSubmissionId The ID of the payment submission to get the details of
     * @param aspspDetails        The details of the ASPSP to send the request to
     * @return The details of the payment submission
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    PaymentSubmissionResponse getPaymentSubmission(String paymentSubmissionId, AspspDetails aspspDetails);
}
