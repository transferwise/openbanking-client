package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.configuration.AspspDetails;

/**
 * An interface specifying the operations for a client supporting TPP registrations with an ASPSP.
 */
public interface RegistrationClient {

    /**
     * Register as a TPP client with an ASPSP.
     *
     * @param clientRegistrationRequest The details (JWT claims) of the registration request
     * @param aspspDetails The details of the ASPSP to send the request to
     * @return The response body from the ASPSP, containing the details of the registration
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    ClientRegistrationResponse registerClient(ClientRegistrationRequest clientRegistrationRequest,
                                              AspspDetails aspspDetails);
}
