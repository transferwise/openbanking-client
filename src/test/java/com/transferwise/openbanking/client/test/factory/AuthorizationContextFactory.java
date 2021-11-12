package com.transferwise.openbanking.client.test.factory;

import com.transferwise.openbanking.client.api.common.AuthorizationContext;

public class AuthorizationContextFactory {

    public static AuthorizationContext aAuthorizationContext() {
        return AuthorizationContext.builder()
            .authorizationCode("authorisation-code")
            .redirectUrl("https://tpp.co.uk")
            .build();
    }

}
