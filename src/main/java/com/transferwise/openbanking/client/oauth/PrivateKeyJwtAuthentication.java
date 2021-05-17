package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import lombok.RequiredArgsConstructor;
import org.jose4j.jwt.JwtClaims;

import java.util.UUID;

/**
 * Supports the 'JWT bearer token' OAuth 2.0 client authentication mechanism.
 *
 * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-jwt-bearer">JWT bear token specification</a>
 */
@RequiredArgsConstructor
public class PrivateKeyJwtAuthentication implements ClientAuthentication {

    private static final String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";

    private static final long CLAIMS_VALID_FOR_MINUTES = 1;

    private final JwtClaimsSigner jwtClaimsSigner;

    @Override
    public ClientAuthenticationMethod getSupportedMethod() {
        return ClientAuthenticationMethod.PRIVATE_KEY_JWT;
    }

    @Override
    public void addClientAuthentication(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails) {
        getAccessTokenRequest.setClientAssertionType(CLIENT_ASSERTION_TYPE);
        getAccessTokenRequest.setClientAssertion(getClientAssertionToken(aspspDetails));
    }

    private String getClientAssertionToken(AspspDetails aspspDetails) {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setIssuer(aspspDetails.getClientId());
        jwtClaims.setSubject(aspspDetails.getClientId());
        jwtClaims.setAudience(aspspDetails.getPrivateKeyJwtAuthenticationAudience());
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setExpirationTimeMinutesInTheFuture(CLAIMS_VALID_FOR_MINUTES);
        jwtClaims.setJwtId(UUID.randomUUID().toString());

        return jwtClaimsSigner.createSignature(jwtClaims, aspspDetails);
    }
}
