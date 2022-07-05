package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteFundsConfirmationResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;

/**
 * An interface specifying the operations for a client supporting version 3 single immediate domestic payments.
 */
public interface PaymentClient {

    /**
     * Create a new domestic payment consent, which can then be authorised and submitted for execution.
     *
     * @param domesticPaymentConsentRequest The details of the payment to setup
     * @param aspspDetails                  The details of the ASPSP to send the request to
     * @param softwareStatementDetails      The details of the software statement that the ASPSP registration uses
     * @return The result, from the ASPSP, of the domestic payment consent request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBWriteDomesticConsentResponse5 createDomesticPaymentConsent(OBWriteDomesticConsent4 domesticPaymentConsentRequest,
                                                                 AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails);

    /**
     * Submits a created and approved immediate domestic payment for execution by the ASPSP.
     * <p>
     * This involves exchanging the provided authorization code for an access token, if the authorization code has
     * already been exchanged, further attempts to exchange it will be rejected by the ASPSP and a
     * {@link com.transferwise.openbanking.client.api.payment.v3.PaymentApiCallException} will be thrown. If the implementation caches
     * or otherwise stores access tokens, then the issue is avoided.
     *
     * @param domesticPaymentRequest   The details of the payment to submit for execution
     * @param authorizationContext     The successful payment authorisation data
     * @param aspspDetails             The details of the ASPSP to send the request to
     * @param softwareStatementDetails The details of the software statement that the ASPSP registration uses
     * @return The result, from the ASPSP, of the domestic payment submission request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBWriteDomesticResponse5 submitDomesticPayment(OBWriteDomestic2 domesticPaymentRequest,
                                                   AuthorizationContext authorizationContext,
                                                   AspspDetails aspspDetails,
                                                   SoftwareStatementDetails softwareStatementDetails);

    /**
     * Get the details of a previously created domestic payment consent.
     *
     * @param consentId    The ID of the domestic payment consent to get the details of
     * @param aspspDetails The details of the ASPSP to send the request to
     * @return The details of the domestic payment consent
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBWriteDomesticConsentResponse5 getDomesticPaymentConsent(String consentId, AspspDetails aspspDetails);

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
    OBWriteDomesticResponse5 getDomesticPayment(String domesticPaymentId, AspspDetails aspspDetails);

    /**
     * Get confirmation of whether not funds are available for a domestic payment consent, which has been authorised
     * but not yet consumed.
     * <p>
     * This involves exchanging the provided authorization code for an access token, if the authorization code has
     * already been exchanged, further attempts to exchange it will be rejected by the ASPSP and a
     * {@link com.transferwise.openbanking.client.api.payment.v3.PaymentApiCallException} will be thrown. If the implementation caches
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
    OBWriteFundsConfirmationResponse1 getFundsConfirmation(String consentId,
                                                           AuthorizationContext authorizationContext,
                                                           AspspDetails aspspDetails);
}
