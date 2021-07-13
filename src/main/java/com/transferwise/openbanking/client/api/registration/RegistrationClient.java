package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;

/**
 * An interface specifying the operations for a client supporting TPP registrations with an ASPSP.
 */
public interface RegistrationClient {

    /**
     * Register as a TPP client with an ASPSP.
     *
     * @param clientRegistrationRequest The details (JWT claims) of the registration request
     * @param aspspDetails              The details of the ASPSP to send the request to
     * @return The response body from the ASPSP, containing the details of the registration
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    ClientRegistrationResponse registerClient(ClientRegistrationRequest clientRegistrationRequest,
                                              AspspDetails aspspDetails);

    /**
     * Update an existing TPP client registration with an ASPSP.
     *
     * @param clientRegistrationRequest The details (JWT claims) of the new registration request, this MUST contain
     *                                  both the claims to change, and those which are unchanged
     * @param aspspDetails              The details of the ASPSP to send the request to
     * @param softwareStatementDetails  The details of the software statement that the ASPSP registration currently uses
     * @return The response body from the ASPSP, containing the details of the updated registration
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    ClientRegistrationResponse updateRegistration(ClientRegistrationRequest clientRegistrationRequest,
                                                  AspspDetails aspspDetails,
                                                  SoftwareStatementDetails softwareStatementDetails);

    /**
     * Delete an existing TPP client registration with an ASPSP.
     *
     * @param aspspDetails              The details of the ASPSP to send the request to
     * @param softwareStatementDetails  The details of the software statement that the ASPSP registration currently uses
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    void deleteRegistration(AspspDetails aspspDetails, SoftwareStatementDetails softwareStatementDetails);
}
