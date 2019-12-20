package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentResponse;
import com.transferwise.openbanking.client.aspsp.AspspDetails;

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
     *
     * @param domesticPaymentRequest The details of the payment to submit for execution
     * @param authorizationCode      The payment authorization code returned by the ASPSP, as a result of a successful
     *                               payment authorization by the account holder
     * @param aspspDetails           The details of the ASPSP to send the request to
     * @return The result, from the ASPSP, of the domestic payment submission request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    DomesticPaymentResponse submitDomesticPayment(DomesticPaymentRequest domesticPaymentRequest,
                                                  String authorizationCode,
                                                  AspspDetails aspspDetails);

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
}
