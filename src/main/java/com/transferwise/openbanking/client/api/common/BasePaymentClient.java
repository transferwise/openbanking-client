package com.transferwise.openbanking.client.api.common;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.json.JsonConverter;
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

    protected final RestOperations restOperations;
    protected final JsonConverter jsonConverter;

    private final OAuthClient oAuthClient;

    public AccessTokenResponse exchangeAuthorizationCode(AuthorizationContext authorizationContext, AspspDetails aspspDetails) {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.authorizationCodeRequest(
            authorizationContext.getAuthorizationCode(),
            authorizationContext.getRedirectUrl());
        return getAccessToken(getAccessTokenRequest, aspspDetails);
    }

    public AccessTokenResponse exchangeRefreshToken(String refreshToken, AspspDetails aspspDetails) {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.refreshTokenRequest(
            refreshToken);
        return getAccessToken(getAccessTokenRequest, aspspDetails);
    }

    protected String getClientCredentialsToken(AspspDetails aspspDetails) {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest(PAYMENTS_SCOPE);
        return getAccessToken(getAccessTokenRequest, aspspDetails).getAccessToken();
    }

    protected String generateApiUrl(String url, String resource, AspspDetails aspspDetails) {
        return String.format(url,
            aspspDetails.getApiBaseUrl("3", resource),
            aspspDetails.getPaymentApiMinorVersion(),
            resource);
    }

    protected String generateVrpApiUrl(String url, String resource, AspspDetails aspspDetails) {
        return String.format(url,
            aspspDetails.getApiBaseUrl("3", resource),
            aspspDetails.getPaymentApiMinorVersion(),
            aspspDetails.getVrpBaseResourceName(),
            resource);
    }

    private AccessTokenResponse getAccessToken(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails) {
        return oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);
    }
}
