package com.transferwise.openbanking.client.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Supported OpenID client authentication methods, for ASPSP access token requests.
 *
 * @see
 * <a href="http://openid.net/specs/openid-connect-core-1_0.html#ClientAuthentication">OpenID Client Authentication</a>
 */
@RequiredArgsConstructor
@Getter
public enum ClientAuthenticationMethod {
    PRIVATE_KEY_JWT("private_key_jwt"),
    CLIENT_SECRET_BASIC("client_secret_basic"),
    CLIENT_SECRET_POST("client_secret_post"),
    TLS_CLIENT_AUTH("tls_client_auth");

    /**
     * The OpenID method name.
     */
    private final String methodName;
}
