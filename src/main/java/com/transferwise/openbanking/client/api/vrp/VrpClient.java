package com.transferwise.openbanking.client.api.vrp;

import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPDetails;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;

/**
 * An interface specifying the operations for a client supporting version 3.1.9 domestic variable recurring payments.
 */
public interface VrpClient {

    /**
     * Create a domestic VRP
     *
     * @param vrpRequest               The details of the domestic VRP to setup
     * @param authorizationContext     The successful payment authorisation data
     * @param aspspDetails             The details of the ASPSP to send the request to
     * @param softwareStatementDetails The details of the software statement that the ASPSP registration uses
     * @return OBDomesticVRPResponse
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBDomesticVRPResponse submitDomesticVrp(
        OBDomesticVRPRequest vrpRequest,
        AuthorizationContext authorizationContext,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    );

    /**
     * Retrieve a domestic VRP
     *
     * @param domesticVrpId The ID of the domestic VRP to get the details of
     * @param aspspDetails  The details of the ASPSP to send the request to
     * @return OBDomesticVRPResponse
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBDomesticVRPResponse getDomesticVrp(String domesticVrpId, AspspDetails aspspDetails);

    /**
     * Retrieve a domestic VRP details
     *
     * @param domesticVrpId The ID of the domestic VRP to get the details of
     * @param aspspDetails  The details of the ASPSP to send the request to
     * @return OBDomesticVRPDetails
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    OBDomesticVRPDetails getDomesticVrpDetails(String domesticVrpId, AspspDetails aspspDetails);
}
