package com.transferwise.openbanking.client.oauth.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@ToString
public class GetAccessTokenRequest {

    public static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
    public static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";

    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String SCOPE_PARAM = "scope";
    private static final String CODE_PARAM = "code";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String CLIENT_SECRET_PARAM = "client_secret";
    private static final String CLIENT_ASSERTION_TYPE_PARAM = "client_assertion_type";
    private static final String CLIENT_ASSERTION_PARAM = "client_assertion";

    private final Map<String, String> requestBody = new HashMap<>();
    private final FapiHeaders requestHeaders = FapiHeaders.defaultHeaders();

    public static GetAccessTokenRequest clientCredentialsRequest(String scope) {
        return new GetAccessTokenRequest()
            .setGrantType(CLIENT_CREDENTIALS_GRANT_TYPE)
            .setScope(scope);
    }

    public static GetAccessTokenRequest authorizationCodeRequest(String authorisationCode, String redirectUri) {
        return new GetAccessTokenRequest()
            .setGrantType(AUTHORIZATION_CODE_GRANT_TYPE)
            .setAuthorisationCode(authorisationCode)
            .setRedirectUri(redirectUri);
    }

    public GetAccessTokenRequest setGrantType(String grantType) {
        requestBody.put(GRANT_TYPE_PARAM, grantType);
        return this;
    }

    public String getGrantType() {
        return requestBody.get(GRANT_TYPE_PARAM);
    }

    public GetAccessTokenRequest setScope(String scope) {
        requestBody.put(SCOPE_PARAM, scope);
        return this;
    }

    public GetAccessTokenRequest setAuthorisationCode(String authorisationCode) {
        requestBody.put(CODE_PARAM, authorisationCode);
        return this;
    }

    public GetAccessTokenRequest setRedirectUri(String redirectUri) {
        requestBody.put(REDIRECT_URI_PARAM, redirectUri);
        return this;
    }

    public GetAccessTokenRequest setClientId(String clientId) {
        requestBody.put(CLIENT_ID_PARAM, clientId);
        return this;
    }

    public GetAccessTokenRequest setClientSecret(String clientSecret) {
        requestBody.put(CLIENT_SECRET_PARAM, clientSecret);
        return this;
    }

    public GetAccessTokenRequest setClientAssertionType(String clientAssertionType) {
        requestBody.put(CLIENT_ASSERTION_TYPE_PARAM, clientAssertionType);
        return this;
    }

    public GetAccessTokenRequest setClientAssertion(String clientAssertion) {
        requestBody.put(CLIENT_ASSERTION_PARAM, clientAssertion);
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
}
