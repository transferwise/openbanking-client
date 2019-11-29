package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.aspsp.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TlsAuthenticationTest {

    private TlsAuthentication tlsAuthentication;

    @BeforeEach
    void init() {
        tlsAuthentication = new TlsAuthentication();
    }

    @Test
    void getSupportedMethod() {
        Assertions.assertEquals(ClientAuthenticationMethod.TLS_CLIENT_AUTH, tlsAuthentication.getSupportedMethod());
    }

    @Test
    void addClientAuthentication() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = aAspspDefinition();

        tlsAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        Assertions.assertEquals(aspspDetails.getClientId(), getAccessTokenRequest.getRequestBody().get("client_id"));
    }

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .clientId("client-id")
            .build();
    }
}
