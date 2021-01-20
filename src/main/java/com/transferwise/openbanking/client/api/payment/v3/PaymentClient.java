package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.payment.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.FundsConfirmationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;

/**
 * An interface specifying the operations for a client supporting version 3 single immediate domestic payments.
 */
public interface PaymentClient {

    /**
     * Create a new domestic payment consent, which can then be authorised and submitted for execution.
     *
     * @param domesticPaymentConsentRequest The details of the payment to setup
     * @param aspspDetails                  The details of the ASPSP to send the request to
     * @return The result, from the ASPSP, of the domestic payment consent request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    DomesticPaymentConsentResponse createDomesticPaymentConsent(
        DomesticPaymentConsentRequest domesticPaymentConsentRequest,
        AspspDetails aspspDetails);

    /**
     * Submits a created and approved immediate domestic payment for execution by the ASPSP.
     * <p>
     * This involves exchanging the provided authorization code for an access token, if the authorization code has
     * already been exchanged, further attempts to exchange it will be rejected by the ASPSP and a
     * {@link com.transferwise.openbanking.client.error.ApiCallException} will be thrown. If the implementation caches
     * or otherwise stores access tokens, then the issue is avoided.
     *
     * @param domesticPaymentRequest The details of the payment to submit for execution
     * @param authorizationContext   The successful payment authorisation data
     * @param aspspDetails           The details of the ASPSP to send the request to
     * @return The result, from the ASPSP, of the domestic payment submission request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    DomesticPaymentResponse submitDomesticPayment(DomesticPaymentRequest domesticPaymentRequest,
                                                  AuthorizationContext authorizationContext,
                                                  AspspDetails aspspDetails);

    /**
     * Get the details of a previously created domestic payment consent.
     *
     * @param consentId The ID of the domestic payment consent to get the details of
     * @param aspspDetails The details of the ASPSP to send the request to
     * @return The details of the domestic payment consent
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    DomesticPaymentConsentResponse getDomesticPaymentConsent(String consentId, AspspDetails aspspDetails);

    /**
     * Get the details of a previously submitted domestic payment.
     *
     * @param domesticPaymentId The ID of the domestic payment to get the details of
     * @param aspspDetails      The details of the ASPSP to send the request to
     * @return The details of the domestic payment
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    DomesticPaymentResponse getDomesticPayment(String domesticPaymentId, AspspDetails aspspDetails);

    /**
     * Get confirmation of whether not funds are available for a domestic payment consent, which has been authorised
     * but not yet consumed.
     * <p>
     * This involves exchanging the provided authorization code for an access token, if the authorization code has
     * already been exchanged, further attempts to exchange it will be rejected by the ASPSP and a
     * {@link com.transferwise.openbanking.client.error.ApiCallException} will be thrown. If the implementation caches
     * or otherwise stores access tokens, then the issue is avoided.
     *
     * @param consentId            The ID of the domestic payment consent to get the funds confirmation for
     * @param authorizationContext The successful payment authorisation data
     * @param aspspDetails         The details of the ASPSP to send the request to
     * @return The confirmation of funds for the domestic payment consent
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    FundsConfirmationResponse getFundsConfirmation(String consentId,
                                                   AuthorizationContext authorizationContext,
                                                   AspspDetails aspspDetails);
}
