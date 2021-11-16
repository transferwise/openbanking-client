package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.factory.AspspDetailsFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

class ClientSecretBasicAuthenticationTest {

    private ClientSecretBasicAuthentication clientSecretBasicAuthentication;

    @BeforeEach
    void init() {
        clientSecretBasicAuthentication = new ClientSecretBasicAuthentication();
    }

    @Test
    void getSupportedMethod() {
        Assertions.assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
            clientSecretBasicAuthentication.getSupportedMethod());
    }

    @Test
    void addClientAuthentication() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        String expectedBearerToken = Base64Utils.encodeToString(
            (aspspDetails.getClientId() + ":" + aspspDetails.getClientSecret())
                .getBytes(StandardCharsets.UTF_8));
        String expectedAuthorisation = "Basic " + expectedBearerToken;

        clientSecretBasicAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        List<String> authorisationHeaders = getAccessTokenRequest.getRequestHeaders().get(HttpHeaders.AUTHORIZATION);
        Assertions.assertEquals(Collections.singletonList(expectedAuthorisation), authorisationHeaders);
    }
}
