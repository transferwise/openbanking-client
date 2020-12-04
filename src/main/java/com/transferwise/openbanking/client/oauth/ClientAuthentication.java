package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;

/**
 * An interface specifying the operations for adding client authentication data to a OAuth get access token request.
 */
public interface ClientAuthentication {

    /**
     * Get the client authentication method supported by this implementation.
     *
     * @return The supported client authentication method.
     */
    ClientAuthenticationMethod getSupportedMethod();

    /**
     * Add client authentication data to a OAuth get access token request, prior to the request being sent to the ASPSP.
     *
     * @param getAccessTokenRequest The request to add the data to
     * @param aspspDetails The details of the ASPSP the request will be sent to
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem adding to the request
     */
    void addClientAuthentication(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails);
}
