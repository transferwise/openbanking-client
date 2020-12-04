package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;

/**
 * Supports the 'client secret post' OAuth 2.0 client authentication mechanism.
 *
 * @see <a href="https://tools.ietf.org/html/rfc6749">OAuth 2.0 authorisation specification</a>
 */
public class ClientSecretPostAuthentication implements ClientAuthentication {

    @Override
    public ClientAuthenticationMethod getSupportedMethod() {
        return ClientAuthenticationMethod.CLIENT_SECRET_POST;
    }

    @Override
    public void addClientAuthentication(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails) {
        getAccessTokenRequest.setClientId(aspspDetails.getClientId());
        getAccessTokenRequest.setClientSecret(aspspDetails.getClientSecret());
    }
}
