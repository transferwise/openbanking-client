package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.OBErrorResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteFundsConfirmationResponse1;
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
     * @param clientCredentialsToken        The client credentials access token, obtained from the ASPSPs OAuth API, to
     *                                      use for the authorization for this ASPSP API call
     * @param aspspDetails                  The details of the ASPSP to send the request to
     * @param softwareStatementDetails      The details of the software statement that the ASPSP registration uses
     * @return The response, from the ASPSP, to the domestic payment consent request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> createDomesticPaymentConsent(OBWriteDomesticConsent4 domesticPaymentConsentRequest,
                                                                                                String clientCredentialsToken,
                                                                                                AspspDetails aspspDetails,
                                                                                                SoftwareStatementDetails softwareStatementDetails);

    /**
     * Submits a created and approved immediate domestic payment for execution by the ASPSP.
     *
     * @param domesticPaymentRequest   The details of the payment to submit for execution
     * @param authorizationCodeToken   The authorization code access token, obtained from the ASPSPs OAuth API, to use
     *                                 for the authorization for this ASPSP API call
     * @param aspspDetails             The details of the ASPSP to send the request to
     * @param softwareStatementDetails The details of the software statement that the ASPSP registration uses
     * @return The response, from the ASPSP, to the domestic payment submission request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> submitDomesticPayment(OBWriteDomestic2 domesticPaymentRequest,
                                                                                  String authorizationCodeToken,
                                                                                  AspspDetails aspspDetails,
                                                                                  SoftwareStatementDetails softwareStatementDetails);

    /**
     * Get the details of a previously created domestic payment consent.
     *
     * @param consentId              The ID of the domestic payment consent to get the details of
     * @param clientCredentialsToken The client credentials access token, obtained from the ASPSPs OAuth API, to use
     *                               for the authorization for this ASPSP API call
     * @param aspspDetails           The details of the ASPSP to send the request to
     * @return The response, from the ASPSP, to the fetch domestic payment consent request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> getDomesticPaymentConsent(String consentId,
                                                                                             String clientCredentialsToken,
                                                                                             AspspDetails aspspDetails);

    /**
     * Get the details of a previously submitted domestic payment.
     *
     * @param domesticPaymentId      The ID of the domestic payment to get the details of
     * @param clientCredentialsToken The client credentials access token, obtained from the ASPSPs OAuth API, to use
     *                               for the authorization for this ASPSP API call
     * @param aspspDetails           The details of the ASPSP to send the request to
     * @return The response, from the ASPSP, to the fetch domestic payment request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> getDomesticPayment(String domesticPaymentId,
                                                                               String clientCredentialsToken,
                                                                               AspspDetails aspspDetails);

    /**
     * Get confirmation of whether not funds are available for a domestic payment consent, which has been authorised
     * but not yet consumed.
     *
     * @param consentId              The ID of the domestic payment consent to get the funds confirmation for
     * @param authorizationCodeToken The authorization code access token, obtained from the ASPSPs OAuth API, to use
     *                               for the authorization for this ASPSP API call
     * @param aspspDetails           The details of the ASPSP to send the request to
     * @return The response, from the ASPSP, to the confirmation of funds request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<OBWriteFundsConfirmationResponse1, OBErrorResponse1> getFundsConfirmation(String consentId,
                                                                                          String authorizationCodeToken,
                                                                                          AspspDetails aspspDetails);
}
