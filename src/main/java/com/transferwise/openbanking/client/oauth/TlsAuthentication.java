package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;

/**
 * Supports the 'Mutual TLS (MTLS)' OAuth 2.0 client authentication mechanism.
 *
 * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-mtls">MTLS specification</a>
 */
public class TlsAuthentication implements ClientAuthentication {

    @Override
    public ClientAuthenticationMethod getSupportedMethod() {
        return ClientAuthenticationMethod.TLS_CLIENT_AUTH;
    }

    @Override
    public void addClientAuthentication(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails) {
        getAccessTokenRequest.setClientId(aspspDetails.getClientId());
    }
}
