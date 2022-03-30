package com.transferwise.openbanking.client.oauth.domain;

import com.transferwise.openbanking.client.oauth.ScopeFormatter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpHeaders;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@ToString
public class GetAccessTokenRequest {

    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String SCOPE_PARAM = "scope";
    private static final String CODE_PARAM = "code";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String CLIENT_SECRET_PARAM = "client_secret";
    private static final String CLIENT_ASSERTION_TYPE_PARAM = "client_assertion_type";
    private static final String CLIENT_ASSERTION_PARAM = "client_assertion";
    private static final String REFRESH_TOKEN_PARAM = "refresh_token";

    private final Map<String, String> requestBody = new HashMap<>();
    private final FapiHeaders requestHeaders = FapiHeaders.defaultHeaders();

    public static GetAccessTokenRequest clientCredentialsRequest(Collection<Scope> scopes) {
        return clientCredentialsRequest(ScopeFormatter.formatScopes(scopes));
    }

    public static GetAccessTokenRequest clientCredentialsRequest(Scope scope) {
        return clientCredentialsRequest(scope.getValue());
    }

    public static GetAccessTokenRequest clientCredentialsRequest(String scope) {
        return new GetAccessTokenRequest()
            .setGrantType(GrantType.CLIENT_CREDENTIALS.getValue())
            .setScope(scope);
    }

    public static GetAccessTokenRequest authorizationCodeRequest(String authorisationCode, String redirectUri) {
        return new GetAccessTokenRequest()
            .setGrantType(GrantType.AUTHORIZATION_CODE.getValue())
            .setAuthorisationCode(authorisationCode)
            .setRedirectUri(redirectUri);
    }

    public static GetAccessTokenRequest refreshTokenRequest(String refreshToken) {
        return new GetAccessTokenRequest()
            .setGrantType(GrantType.REFRESH_TOKEN.getValue())
            .setRefreshToken(refreshToken);
    }

    public GetAccessTokenRequest setGrantType(String grantType) {
        setBodyParameter(GRANT_TYPE_PARAM, grantType);
        return this;
    }

    public String getGrantType() {
        return requestBody.get(GRANT_TYPE_PARAM);
    }

    public GetAccessTokenRequest setScope(String scope) {
        setBodyParameter(SCOPE_PARAM, scope);
        return this;
    }

    public GetAccessTokenRequest setAuthorisationCode(String authorisationCode) {
        setBodyParameter(CODE_PARAM, authorisationCode);
        return this;
    }

    public GetAccessTokenRequest setRefreshToken(String authorisationCode) {
        setBodyParameter(REFRESH_TOKEN_PARAM, authorisationCode);
        return this;
    }

    public String getAuthorisationCode() {
        return requestBody.get(CODE_PARAM);
    }

    public GetAccessTokenRequest setRedirectUri(String redirectUri) {
        setBodyParameter(REDIRECT_URI_PARAM, redirectUri);
        return this;
    }

    public GetAccessTokenRequest setClientId(String clientId) {
        setBodyParameter(CLIENT_ID_PARAM, clientId);
        return this;
    }

    public GetAccessTokenRequest setClientSecret(String clientSecret) {
        setBodyParameter(CLIENT_SECRET_PARAM, clientSecret);
        return this;
    }

    public GetAccessTokenRequest setClientAssertionType(String clientAssertionType) {
        setBodyParameter(CLIENT_ASSERTION_TYPE_PARAM, clientAssertionType);
        return this;
    }

    public GetAccessTokenRequest setClientAssertion(String clientAssertion) {
        setBodyParameter(CLIENT_ASSERTION_PARAM, clientAssertion);
        return this;
    }

    public GetAccessTokenRequest setAuthorizationHeader(String authorizationHeader) {
        requestHeaders.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return this;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

    public FapiHeaders getRequestHeaders() {
        return requestHeaders;
    }

    private void setBodyParameter(String key, String value) {
        if (value != null && !value.isBlank()) {
            requestBody.put(key, value);
        }
    }
}
