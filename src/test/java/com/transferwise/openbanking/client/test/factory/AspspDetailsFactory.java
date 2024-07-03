package com.transferwise.openbanking.client.test.factory;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import java.util.Set;

@SuppressWarnings("checkstyle:methodname")
public class AspspDetailsFactory {

    public static AspspDetails aTestAspspDetails() {
        return aTestAspspDetails(false);
    }

    public static AspspDetails aTestAspspDetails(boolean registrationUsesJoseContentType) {
        return aTestAspspDetails(registrationUsesJoseContentType, Set.of(Scope.PAYMENTS));
    }

    public static AspspDetails aTestAspspDetails(
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
            .organisationId("organisation-id")
            .build();
    }

    public static AspspDetails aTestAspspDetails(
        String apiBaseUrl
    ) {
        return TestAspspDetails.builder()
            .apiBaseUrl(apiBaseUrl)
            .registrationUrl("/registration-url")
            .tokenUrl("/token-url")
            .paymentApiMinorVersion("1")
            .registrationUsesJoseContentType(false)
            .registrationAuthenticationScopes(Set.of(Scope.PAYMENTS))
            .clientId("client-id")
            .clientSecret("client-secret")
            .organisationId("organisation-id")
            .build();
    }
}
