package com.transferwise.openbanking.client.api.payment.common;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.TppConfiguration;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestOperations;

/**
 * Base class for all payments API clients, containing common functionality, in particular around OAuth functionality.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BasePaymentClient {

    private static final String PAYMENTS_SCOPE = "payments";

    protected final TppConfiguration tppConfiguration;
    protected final RestOperations restOperations;

    private final OAuthClient oAuthClient;

    protected String getClientCredentialsToken(AspspDetails aspspDetails) {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest(PAYMENTS_SCOPE);
        return getAccessToken(getAccessTokenRequest, aspspDetails);
    }

    protected String exchangeAuthorizationCode(String authorizationCode, AspspDetails aspspDetails) {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.authorizationCodeRequest(authorizationCode,
            tppConfiguration.getRedirectUrl());
        return getAccessToken(getAccessTokenRequest, aspspDetails);
    }

    private String getAccessToken(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails) {
        AccessTokenResponse accessTokenResponse = oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);
        return accessTokenResponse.getAccessToken();
    }
}
