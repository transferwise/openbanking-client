package com.transferwise.openbanking.client.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.transferwise.openbanking.client.error.ClientException;
import lombok.RequiredArgsConstructor;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import java.security.Key;

/**
 * Provides functionality for signing JWT claims, for use in requests to ASPSPs.
 */
@RequiredArgsConstructor
public class JwtClaimsSigner {

    private final ObjectMapper objectMapper;
    private final Key signingPrivateKey;
    private final String signingCertificateId;

    public JwtClaimsSigner(Key signingPrivateKey, String signingCertificateId) {
        this.signingPrivateKey = signingPrivateKey;
        this.signingCertificateId = signingCertificateId;
        this.objectMapper = defaultObjectMapper();
    }

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }

    /**
     * Sign the given claims to produce a JWS string.
     *
     * @param jwtClaims        The JWT claims to sign
     * @param signingAlgorithm The algorithm to use for the signing, see {@link org.jose4j.jws.AlgorithmIdentifiers}
     *                         for possible values
     * @return The signed claims as a compact and URL friendly string
     */
    public String signPayload(JwtClaims jwtClaims, String signingAlgorithm) {
        return signJsonPayload(jwtClaims.toJson(), signingAlgorithm);
    }

    /**
     * Sign the given claims to produce a JWS string.
     * <p>
     * This method provides full flexibility in the claims that can be signed, the Jackson JSON {@link ObjectMapper}
     * class will be used to convert the claims to a JSON string prior to signing.
     *
     * @param jwtClaims        The JWT claims to sign
     * @param signingAlgorithm The algorithm to use for the signing, see {@link org.jose4j.jws.AlgorithmIdentifiers}
     *                         for possible values
     * @return The signed claims as a compact and URL friendly string
     */
    public String signPayload(Object jwtClaims, String signingAlgorithm) {
        try {
            return signJsonPayload(objectMapper.writeValueAsString(jwtClaims), signingAlgorithm);
        } catch (JsonProcessingException e) {
            throw new ClientException("Unable to serialise JWT claims", e);
        }
    }

    private String signJsonPayload(String payload, String signingAlgorithm) {
        JsonWebSignature jsonWebSignature = new JsonWebSignature();
        jsonWebSignature.setPayload(payload);
        jsonWebSignature.setKey(signingPrivateKey);
        jsonWebSignature.setAlgorithmHeaderValue(signingAlgorithm);
        jsonWebSignature.setKeyIdHeaderValue(signingCertificateId);

        try {
            return jsonWebSignature.getCompactSerialization();
        } catch (JoseException e) {
            throw new ClientException("Unable to serialize JSON web signature", e);
        }
    }
}
