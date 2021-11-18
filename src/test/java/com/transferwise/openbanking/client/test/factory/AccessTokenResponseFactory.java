package com.transferwise.openbanking.client.test.factory;

import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;

public class AccessTokenResponseFactory {

    public static AccessTokenResponse aAccessTokenResponse() {
        return AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
    }
}
