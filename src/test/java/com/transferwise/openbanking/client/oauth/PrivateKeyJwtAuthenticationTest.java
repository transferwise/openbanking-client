package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnitTestContainsTooManyAsserts"})
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
        assertEquals(ClientAuthenticationMethod.PRIVATE_KEY_JWT,
            privateKeyJwtAuthentication.getSupportedMethod());
    }

    @Test
    void addClientAuthentication() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = aAspspDefinition();

        String signedPayload = "signed-payload";
        when(jwtClaimsSigner.createSignature(
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
            eq(aspspDetails)))
            .thenReturn(signedPayload);

        privateKeyJwtAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        assertEquals("urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
            getAccessTokenRequest.getRequestBody().get("client_assertion_type"));
        assertEquals(signedPayload, getAccessTokenRequest.getRequestBody().get("client_assertion"));
    }

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .clientId("client-id")
            .privateKeyJwtAuthenticationAudience("/token-issuer-url")
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .build();
    }

    @Test
    void addClientAuthentication_defaultAspsDetails() throws Exception {
        // Given
        GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest();
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .clientId("aClientId")
            .privateKeyJwtAuthenticationAudience("/autenticationAudienceUrl")
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .build();

        when(jwtClaimsSigner.createSignature(any(JwtClaims.class), any(AspspDetails.class))).thenReturn("aJwtSignature");

        // When
        privateKeyJwtAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        // Then
        assertEquals("urn:ietf:params:oauth:client-assertion-type:jwt-bearer", getAccessTokenRequest.getRequestBody().get("client_assertion_type"));
        assertEquals("aJwtSignature", getAccessTokenRequest.getRequestBody().get("client_assertion"));

        ArgumentCaptor<JwtClaims> jwtClaimsCaptor = ArgumentCaptor.forClass(JwtClaims.class);
        verify(jwtClaimsSigner).createSignature(jwtClaimsCaptor.capture(), eq(aspspDetails));

        JwtClaims jwtClaims = jwtClaimsCaptor.getValue();
        assertEquals("aClientId", jwtClaims.getIssuer());
        assertEquals("aClientId", jwtClaims.getSubject());
        assertEquals(1, jwtClaims.getAudience().size());
        assertEquals("/autenticationAudienceUrl", jwtClaims.getAudience().get(0));
        assertNotNull(jwtClaims.getIssuedAt());
        assertNotNull(jwtClaims.getExpirationTime());
        assertNotNull(jwtClaims.getJwtId());
    }

    @Test
    void addClientAuthentication_additionalJwtAspsDetails() throws Exception {
        // Given
        GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest();
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .clientId("aClientId")
            .privateKeyJwtAuthenticationAudience("/autenticationAudienceUrl")
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .issuer("/anIssuer")
            .build();

        when(jwtClaimsSigner.createSignature(any(JwtClaims.class), any(AspspDetails.class))).thenReturn("aJwtSignature");

        // When
        privateKeyJwtAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        // Then
        assertEquals("urn:ietf:params:oauth:client-assertion-type:jwt-bearer", getAccessTokenRequest.getRequestBody().get("client_assertion_type"));
        assertEquals("aJwtSignature", getAccessTokenRequest.getRequestBody().get("client_assertion"));

        ArgumentCaptor<JwtClaims> jwtClaimsCaptor = ArgumentCaptor.forClass(JwtClaims.class);
        verify(jwtClaimsSigner).createSignature(jwtClaimsCaptor.capture(), eq(aspspDetails));

        JwtClaims jwtClaims = jwtClaimsCaptor.getValue();
        assertEquals("/anIssuer", jwtClaims.getIssuer());
        assertEquals("aClientId", jwtClaims.getSubject());
        assertEquals(1, jwtClaims.getAudience().size());
        assertEquals("/autenticationAudienceUrl", jwtClaims.getAudience().get(0));
        assertNotNull(jwtClaims.getIssuedAt());
        assertNotNull(jwtClaims.getExpirationTime());
        assertNotNull(jwtClaims.getJwtId());
    }
}
