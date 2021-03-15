package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.MalformedClaimException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PrivateKeyJwtAuthenticationTest {

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private PrivateKeyJwtAuthentication privateKeyJwtAuthentication;

    @BeforeEach
    void init() {
        privateKeyJwtAuthentication = new PrivateKeyJwtAuthentication(jwtClaimsSigner);
    }

    @Test
    void getSupportedMethod() {
        Assertions.assertEquals(ClientAuthenticationMethod.PRIVATE_KEY_JWT,
            privateKeyJwtAuthentication.getSupportedMethod());
    }

    @Test
    void addClientAuthentication() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = aAspspDefinition();

        String signedPayload = "signed-payload";
        Mockito.when(jwtClaimsSigner.createSignature(
            Mockito.argThat(jwtClaims -> {
                try {
                    return jwtClaims.getIssuer().equals(aspspDetails.getClientId()) &&
                        jwtClaims.getSubject().equals(aspspDetails.getClientId()) &&
                        jwtClaims.getAudience().equals(List.of(aspspDetails.getPrivateKeyJwtAuthenticationAudience())) &&
                        jwtClaims.getIssuedAt() != null &&
                        jwtClaims.getExpirationTime().isAfter(jwtClaims.getIssuedAt()) &&
                        jwtClaims.getJwtId() != null;
                } catch (MalformedClaimException e) {
                    throw new RuntimeException(e);
                }
            }),
            Mockito.eq(aspspDetails)))
            .thenReturn(signedPayload);

        privateKeyJwtAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        Assertions.assertEquals("urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
            getAccessTokenRequest.getRequestBody().get("client_assertion_type"));
        Assertions.assertEquals(signedPayload, getAccessTokenRequest.getRequestBody().get("client_assertion"));
    }

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .clientId("client-id")
            .privateKeyJwtAuthenticationAudience("/token-issuer-url")
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .build();
    }
}
