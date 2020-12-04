package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

/**
 * Supports the 'client secret basic' OAuth 2.0 client authentication mechanism.
 *
 * @see <a href="https://tools.ietf.org/html/rfc6749">OAuth 2.0 authorisation specification</a>
 */
public class ClientSecretBasicAuthentication implements ClientAuthentication {

    @Override
    public ClientAuthenticationMethod getSupportedMethod() {
        return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
    }

    @Override
    public void addClientAuthentication(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails) {
        String clientAuthorisationToken = Base64Utils.encodeToString(
            (aspspDetails.getClientId() + ":" + aspspDetails.getClientSecret())
                .getBytes(StandardCharsets.UTF_8));

        getAccessTokenRequest.setAuthorizationHeader("Basic " + clientAuthorisationToken);
    }
}
