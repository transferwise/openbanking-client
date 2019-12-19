package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.aspsp.AspspDetails;

/**
 * An interface specifying the operations for a client supporting TPP registrations with an ASPSP.
 */
public interface RegistrationClient {

    /**
     * Register as a TPP client with an ASPSP.
     *
     * @param signedClaims The details (JWT claims) of the registration request, signed with the TPP's signing key
     * @param aspspDetails The details of the ASPSP to send the request to
     * @return The complete response body from the ASPSP, containing the details of the registration
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    String registerClient(String signedClaims, AspspDetails aspspDetails);
}
