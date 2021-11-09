package com.transferwise.openbanking.client.api.vrp;

import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;

/**
 * An interface specifying the operations for a client supporting version 3.1.9 domestic variable recurring payments consents.
 */
public interface VrpConsentClient {

    /**
     * Create a domestic VRP consent
     *
     * @param domesticVRPConsentRequest The details of the VRP consent to setup
     * @param aspspDetails              The details of the ASPSP to send the request to
     * @param softwareStatementDetails  The details of the software statement that the ASPSP registration uses
     * @return OBDomesticVRPConsentResponse
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBDomesticVRPConsentResponse createDomesticVrpConsent(OBDomesticVRPConsentRequest domesticVRPConsentRequest,
                                                          AspspDetails aspspDetails,
                                                          SoftwareStatementDetails softwareStatementDetails);

    /**
     * Get confirmation of whether not funds are available for a domestic VRP consent, which has been authorised
     * but not yet consumed.
     * <p>
     * This involves exchanging the provided authorization code for an access token, if the authorization code has
     * already been exchanged, further attempts to exchange it will be rejected by the ASPSP and a
     * {@link com.transferwise.openbanking.client.error.ApiCallException} will be thrown. If the implementation caches
     * or otherwise stores access tokens, then the issue is avoided.
     *
     * @param consentId                     The ID of the domestic VRP consent to get the details of
     * @param obVRPFundsConfirmationRequest The details of the VRP funds confirmation request
     * @param authorizationContext          The successful consent authorisation data
     * @param aspspDetails                  The details of the ASPSP to send the request to
     * @return OBVRPFundsConfirmationResponse
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBVRPFundsConfirmationResponse getFundsConfirmation(String consentId,
                                                        OBVRPFundsConfirmationRequest obVRPFundsConfirmationRequest,
                                                        AuthorizationContext authorizationContext,
                                                        AspspDetails aspspDetails);

    /**
     * Retrieve a domestic VRP consent
     *
     * @param consentId    ConsentId
     * @param aspspDetails The details of the ASPSP to send the request to
     * @return OBDomesticVRPConsentResponse
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBDomesticVRPConsentResponse getDomesticVrpConsent(String consentId, AspspDetails aspspDetails);

    /**
     * Delete a domestic VRP
     *
     * @param consentId ConsentId
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    void deleteDomesticVrpConsent(String consentId, AspspDetails aspspDetails);
}
