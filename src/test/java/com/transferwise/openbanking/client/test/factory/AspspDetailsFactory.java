package com.transferwise.openbanking.client.test.factory;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.test.TestAspspDetails;

import java.util.Set;

public class AspspDetailsFactory {

    public static AspspDetails aAspspDetails() {
        return aAspspDetails(false);
    }

    public static AspspDetails aAspspDetails(boolean registrationUsesJoseContentType) {
        return aAspspDetails(registrationUsesJoseContentType, Set.of(Scope.PAYMENTS));
    }

    public static AspspDetails aAspspDetails(
        boolean registrationUsesJoseContentType,
        Set<Scope> registrationAuthenticationScopes
    ) {
        return TestAspspDetails.builder()
            .apiBaseUrl("https://aspsp.co.uk")
            .registrationUrl("/registration-url")
            .tokenUrl("/token-url")
            .paymentApiMinorVersion("1")
            .registrationUsesJoseContentType(registrationUsesJoseContentType)
            .registrationAuthenticationScopes(registrationAuthenticationScopes)
            .clientId("client-id")
            .clientSecret("client-secret")
            .build();
    }
}
