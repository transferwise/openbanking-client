package com.transferwise.openbanking.client.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.TppConfiguration;
import com.transferwise.openbanking.client.error.ClientException;
import com.transferwise.openbanking.client.security.KeySupplier;
import lombok.RequiredArgsConstructor;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.lang.JoseException;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Provides functionality for signing JWT claims, for use in requests to ASPSPs.
 */
@RequiredArgsConstructor
public class JwtClaimsSigner {

    private static final String TRUST_ANCHOR_VALUE = "openbanking.org.uk";

    private static final String X509_CERTIFICATE_TYPE = "X.509";

    private final KeySupplier keySupplier;
    private final TppConfiguration tppConfiguration;
    private final ObjectMapper objectMapper;

    public JwtClaimsSigner(KeySupplier keySupplier, TppConfiguration tppConfiguration) {
        this.keySupplier = keySupplier;
        this.tppConfiguration = tppConfiguration;
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
     * @param jwtClaims    The JWT claims to sign
     * @param aspspDetails The details of the ASPSP which the JWS will be sent to
     * @return The signed claims as a compact and URL friendly string
     */
    public String createSignature(JwtClaims jwtClaims, AspspDetails aspspDetails) {
        return signJsonPayload(jwtClaims.toJson(), aspspDetails);
    }

    /**
     * Sign the given claims to produce a JWS string.
     * <p>
     * This method provides full flexibility in the claims that can be signed, the Jackson JSON {@link ObjectMapper}
     * class will be used to convert the claims to a JSON string prior to signing.
     *
     * @param jwtClaims    The JWT claims to sign
     * @param aspspDetails The details of the ASPSP which the JWS will be sent to
     * @return The signed claims as a compact and URL friendly string
     */
    public String createSignature(Object jwtClaims, AspspDetails aspspDetails) {
        try {
            return signJsonPayload(objectMapper.writeValueAsString(jwtClaims), aspspDetails);
        } catch (JsonProcessingException e) {
            throw new ClientException("Unable to serialise JWT claims", e);
        }
    }

    /**
     * Sign the given claims to produce JWS string, with a detached payload (the claims).
     * <p>
     * This means the signature is created with the payload present, but the compact serialised JWS does not include
     * the payload, to allow the payload to be checked for modification in transit using the signature.
     *
     * @param jwtClaims    The JWT claims to sign
     * @param aspspDetails The details of the ASPSP which the JWS will be sent to
     * @return The signed claims as a detached compact and URL friendly string.
     */
    public String createDetachedSignature(Object jwtClaims, AspspDetails aspspDetails) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(jwtClaims);
        } catch (JsonProcessingException e) {
            throw new ClientException("Unable to serialise JWT claims", e);
        }

        JsonWebSignature jsonWebSignature = new JsonWebSignature();
        jsonWebSignature.setPayload(payload);
        jsonWebSignature.setKey(keySupplier.getSigningKey(aspspDetails));
        jsonWebSignature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_PSS_USING_SHA256);
        jsonWebSignature.setKeyIdHeaderValue(aspspDetails.getSigningKeyId());
        jsonWebSignature.setHeader(OpenBankingJwsHeaders.OPEN_BANKING_IAT, NumericDate.now().getValue());
        jsonWebSignature.setHeader(OpenBankingJwsHeaders.OPEN_BANKING_ISS, generateIssClaim(aspspDetails));
        jsonWebSignature.setHeader(OpenBankingJwsHeaders.OPEN_BANKING_TAN, TRUST_ANCHOR_VALUE);

        if (aspspDetails.detachedSignaturesRequireB64Header()) {
            jsonWebSignature.setHeader(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD, false);
            jsonWebSignature.setCriticalHeaderNames(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD,
                OpenBankingJwsHeaders.OPEN_BANKING_IAT,
                OpenBankingJwsHeaders.OPEN_BANKING_ISS,
                OpenBankingJwsHeaders.OPEN_BANKING_TAN);
        } else {
            jsonWebSignature.setCriticalHeaderNames(OpenBankingJwsHeaders.OPEN_BANKING_IAT,
                OpenBankingJwsHeaders.OPEN_BANKING_ISS,
                OpenBankingJwsHeaders.OPEN_BANKING_TAN);
        }

        try {
            return jsonWebSignature.getDetachedContentCompactSerialization();
        } catch (JoseException e) {
            throw new ClientException("Unable to create detached JSON web signature", e);
        }
    }

    private String generateIssClaim(AspspDetails aspspDetails) {
        if (aspspDetails.detachedSignatureUsesDirectoryIssFormat()) {
            return tppConfiguration.getOrganisationId() + "/" + tppConfiguration.getSoftwareStatementId();
        } else {
            Certificate signingCertificate = keySupplier.getSigningCertificate(aspspDetails);
            if (X509_CERTIFICATE_TYPE.equalsIgnoreCase(signingCertificate.getType())) {
                return ((X509Certificate) signingCertificate).getSubjectX500Principal().getName();
            } else {
                throw new ClientException(String.format(
                    "Unknown signing certificate type supplied, 'X.509' required, but got '%s'",
                    signingCertificate.getType()));
            }
        }
    }

    private String signJsonPayload(String payload, AspspDetails aspspDetails) {
        JsonWebSignature jsonWebSignature = new JsonWebSignature();
        jsonWebSignature.setPayload(payload);
        jsonWebSignature.setKey(keySupplier.getSigningKey(aspspDetails));
        jsonWebSignature.setAlgorithmHeaderValue(aspspDetails.getSigningAlgorithm());
        jsonWebSignature.setKeyIdHeaderValue(aspspDetails.getSigningKeyId());

        try {
            return jsonWebSignature.getCompactSerialization();
        } catch (JoseException e) {
            throw new ClientException("Unable to create JSON web signature", e);
        }
    }
}
