package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.ErrorResponse;
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
     * @return The response, from the ASPSP, to the access token request
     * @throws com.transferwise.openbanking.client.error.ClientException if there was a problem building the request to
     *                                                                   to the ASPSP, or there was a problem parsing
     *                                                                   the response when the API call succeeded
     */
    ApiResponse<AccessTokenResponse, ErrorResponse> getAccessToken(GetAccessTokenRequest getAccessTokenRequest,
                                                                   AspspDetails aspspDetails);
}
