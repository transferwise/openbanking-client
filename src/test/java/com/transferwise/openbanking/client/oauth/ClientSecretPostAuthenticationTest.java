package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.factory.AspspDetailsFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientSecretPostAuthenticationTest {

    private ClientSecretPostAuthentication clientSecretPostAuthentication;

    @BeforeEach
    void init() {
        clientSecretPostAuthentication = new ClientSecretPostAuthentication();
    }

    @Test
    void getSupportedMethod() {
        Assertions.assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_POST,
            clientSecretPostAuthentication.getSupportedMethod());
    }

    @Test
    void addClientAuthentication() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        clientSecretPostAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        Assertions.assertEquals(aspspDetails.getClientId(), getAccessTokenRequest.getRequestBody().get("client_id"));
        Assertions.assertEquals(aspspDetails.getClientSecret(),
            getAccessTokenRequest.getRequestBody().get("client_secret"));
    }
}
