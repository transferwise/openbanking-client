package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ErrorResponse;
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
     * @return The response, from the ASPSP, to the client registration request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<ClientRegistrationResponse, ErrorResponse> registerClient(ClientRegistrationRequest clientRegistrationRequest,
                                                                          AspspDetails aspspDetails);

    /**
     * Update an existing TPP client registration with an ASPSP.
     *
     * @param clientRegistrationRequest The details (JWT claims) of the new registration request, this MUST contain
     *                                  both the claims to change, and those which are unchanged
     * @param clientCredentialsToken    The client credentials access token, obtained from the ASPSPs OAuth API, to
     *                                  use for the authorization for this ASPSP API call
     * @param aspspDetails              The details of the ASPSP to send the request to
     * @param softwareStatementDetails  The details of the software statement that the ASPSP registration currently uses
     * @return The response, from the ASPSP, to the update client registration request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<ClientRegistrationResponse, ErrorResponse> updateRegistration(ClientRegistrationRequest clientRegistrationRequest,
                                                                              String clientCredentialsToken,
                                                                              AspspDetails aspspDetails,
                                                                              SoftwareStatementDetails softwareStatementDetails);
}
