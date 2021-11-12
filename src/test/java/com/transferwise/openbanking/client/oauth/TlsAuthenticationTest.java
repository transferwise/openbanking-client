package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.transferwise.openbanking.client.test.factory.AspspDetailsFactory.aAspspDetails;

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
        AspspDetails aspspDetails = aAspspDetails();

        tlsAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        Assertions.assertEquals(aspspDetails.getClientId(), getAccessTokenRequest.getRequestBody().get("client_id"));
    }
}
