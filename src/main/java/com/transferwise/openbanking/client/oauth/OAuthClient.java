package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;

/**
 * An interface specifying the operations for a client supporting getting OAuth access tokens.
 */
public interface OAuthClient {

    /**
     * Get an OAuth2 access token from an ASPSP.
     *
     * @param getAccessTokenRequest The details of the token to get
     * @param aspspDetails          The details of the ASPSP to send the request to
     * @return The result, from the ASPSP, of the access token request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request(s)
     *                                                                   to the ASPSP or the HTTP call to the ASPSP
     *                                                                   failed
     */
    AccessTokenResponse getAccessToken(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails);
}
