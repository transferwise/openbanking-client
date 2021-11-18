package com.transferwise.openbanking.client.jwt;

import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2DataInitiationInstructedAmount;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JacksonJsonConverter;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.security.KeySupplier;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import com.transferwise.openbanking.client.test.TestKeyUtils;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwx.HeaderParameterNames;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;

class JwtClaimsSignerTest {

    private static final AlgorithmConstraints PS256_ALGORITHM = new AlgorithmConstraints(
        AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_PSS_USING_SHA256);

    private static KeyPair keyPair;
    private static X509Certificate certificate;

    private static JsonConverter jsonConverter;

    private KeySupplier keySupplier;

    private JwtClaimsSigner jwtClaimsSigner;

    @BeforeAll
    static void initAll() throws Exception {
        keyPair = TestKeyUtils.aKeyPair();
        certificate = TestKeyUtils.aCertificate(keyPair);
        jsonConverter = new JacksonJsonConverter();
    }

    @BeforeEach
    void init() {
        keySupplier = Mockito.mock(KeySupplier.class);

        jwtClaimsSigner = new JwtClaimsSigner(keySupplier, jsonConverter);
    }

    @Test
    void createSignatureWithJose4JPayloadProducesValidSignature() throws Exception {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setStringClaim("a-claim", "a-value");
        Map<String, Object> expectedJwtClaims = Map.of("a-claim", "a-value");
        AspspDetails aspspDetails = aAspspDetails();

        Mockito.when(keySupplier.getSigningKey(aspspDetails)).thenReturn(keyPair.getPrivate());

        String serialisedSignature = jwtClaimsSigner.createSignature(jwtClaims, aspspDetails);
        JsonWebSignature jsonWebSignature = parseSignature(serialisedSignature);

        Assertions.assertEquals(expectedJwtClaims,
            jsonConverter.readValue(jsonWebSignature.getPayload(), Map.class));
        Assertions.assertEquals(aspspDetails.getSigningKeyId(), jsonWebSignature.getKeyIdHeaderValue());
    }

    @Test
    void createSignatureWithCustomPayloadProducesValidSignature() throws Exception {
        OBWriteDomestic2DataInitiationInstructedAmount jwtClaims = anInstructedAmount();
        Map<String, Object> expectedJwtClaims = Map.of("Amount", "10.50", "Currency", "GBP");
        AspspDetails aspspDetails = aAspspDetails();

        Mockito.when(keySupplier.getSigningKey(aspspDetails)).thenReturn(keyPair.getPrivate());

        String serialisedSignature = jwtClaimsSigner.createSignature(jwtClaims, aspspDetails);
        JsonWebSignature jsonWebSignature = parseSignature(serialisedSignature);

        Assertions.assertEquals(expectedJwtClaims,
            jsonConverter.readValue(jsonWebSignature.getPayload(), Map.class));
        Assertions.assertEquals(aspspDetails.getSigningKeyId(), jsonWebSignature.getKeyIdHeaderValue());
    }

    @Test
    void createDetachedSignatureProducesValidSignature() throws Exception {
        OBWriteDomestic2DataInitiationInstructedAmount jwtClaims = anInstructedAmount();
        AspspDetails aspspDetails = aAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(keySupplier.getSigningKey(aspspDetails)).thenReturn(keyPair.getPrivate());

        String serialisedSignature = jwtClaimsSigner.createDetachedSignature(jwtClaims,
            aspspDetails,
            softwareStatementDetails);
        JsonWebSignature jsonWebSignature = parseDetachedSignature(serialisedSignature, jwtClaims);

        Assertions.assertTrue(jsonWebSignature.verifySignature());
        Assertions.assertEquals(aspspDetails.getSigningKeyId(), jsonWebSignature.getKeyIdHeaderValue());
        Assertions.assertEquals(Boolean.FALSE,
            jsonWebSignature.getObjectHeader(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD));
        Assertions.assertEquals(
            softwareStatementDetails.getOrganisationId() + "/" + softwareStatementDetails.getSoftwareStatementId(),
            jsonWebSignature.getHeader(OpenBankingJwsHeaders.OPEN_BANKING_ISS));
        Assertions.assertEquals("openbanking.org.uk",
            jsonWebSignature.getHeader(OpenBankingJwsHeaders.OPEN_BANKING_TAN));

        long generatedAtTimeDifference = NumericDate.now().getValue() -
            ((long) jsonWebSignature.getObjectHeader(OpenBankingJwsHeaders.OPEN_BANKING_IAT));
        Assertions.assertTrue(generatedAtTimeDifference <= 5);

        Assertions.assertEquals(
            Arrays.asList(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD,
                OpenBankingJwsHeaders.OPEN_BANKING_IAT,
                OpenBankingJwsHeaders.OPEN_BANKING_ISS,
                OpenBankingJwsHeaders.OPEN_BANKING_TAN),
            jsonWebSignature.getObjectHeader(HeaderParameterNames.CRITICAL));
    }

    @Test
    void createDetachedSignatureProducesValidSignatureWithNonDirectoryIssFormat() throws Exception {
        OBWriteDomestic2DataInitiationInstructedAmount jwtClaims = anInstructedAmount();
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .signingKeyId("signing-key-id")
            .detachedSignatureUsesDirectoryIssFormat(false)
            .build();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(keySupplier.getSigningKey(aspspDetails)).thenReturn(keyPair.getPrivate());
        Mockito.when(keySupplier.getSigningCertificate(aspspDetails)).thenReturn(certificate);

        String serialisedSignature = jwtClaimsSigner.createDetachedSignature(jwtClaims,
            aspspDetails,
            softwareStatementDetails);
        JsonWebSignature jsonWebSignature = parseDetachedSignature(serialisedSignature, jwtClaims);

        Assertions.assertTrue(jsonWebSignature.verifySignature());
        Assertions.assertEquals(aspspDetails.getSigningKeyId(), jsonWebSignature.getKeyIdHeaderValue());
        Assertions.assertEquals(Boolean.FALSE,
            jsonWebSignature.getObjectHeader(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD));
        Assertions.assertEquals(certificate.getSubjectX500Principal().getName(),
            jsonWebSignature.getHeader(OpenBankingJwsHeaders.OPEN_BANKING_ISS));
        Assertions.assertEquals("openbanking.org.uk",
            jsonWebSignature.getHeader(OpenBankingJwsHeaders.OPEN_BANKING_TAN));

        long generatedAtTimeDifference = NumericDate.now().getValue() -
            ((long) jsonWebSignature.getObjectHeader(OpenBankingJwsHeaders.OPEN_BANKING_IAT));
        Assertions.assertTrue(generatedAtTimeDifference <= 5);

        Assertions.assertEquals(
            Arrays.asList(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD,
                OpenBankingJwsHeaders.OPEN_BANKING_IAT,
                OpenBankingJwsHeaders.OPEN_BANKING_ISS,
                OpenBankingJwsHeaders.OPEN_BANKING_TAN),
            jsonWebSignature.getObjectHeader(HeaderParameterNames.CRITICAL));
    }

    private AspspDetails aAspspDetails() {
        return TestAspspDetails.builder()
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .signingKeyId("signing-key-id")
            .detachedSignatureUsesDirectoryIssFormat(true)
            .build();
    }

    private OBWriteDomestic2DataInitiationInstructedAmount anInstructedAmount() {
        return new OBWriteDomestic2DataInitiationInstructedAmount()
            .amount("10.50")
            .currency("GBP");
    }

    private JsonWebSignature parseSignature(String serialisedSignature) throws Exception {
        JsonWebSignature jsonWebSignature = new JsonWebSignature();
        jsonWebSignature.setCompactSerialization(serialisedSignature);
        jsonWebSignature.setKey(keyPair.getPublic());
        jsonWebSignature.setAlgorithmConstraints(PS256_ALGORITHM);
        return jsonWebSignature;
    }

    private JsonWebSignature parseDetachedSignature(String serialisedSignature, Object detachedPayload)
        throws Exception {
        JsonWebSignature jsonWebSignature = new JsonWebSignature();
        jsonWebSignature.setCompactSerialization(serialisedSignature);
        jsonWebSignature.setPayload(jsonConverter.writeValueAsString(detachedPayload));
        jsonWebSignature.setKey(keyPair.getPublic());
        jsonWebSignature.setAlgorithmConstraints(PS256_ALGORITHM);
        jsonWebSignature.setKnownCriticalHeaders(HeaderParameterNames.BASE64URL_ENCODE_PAYLOAD,
            OpenBankingJwsHeaders.OPEN_BANKING_IAT,
            OpenBankingJwsHeaders.OPEN_BANKING_ISS,
            OpenBankingJwsHeaders.OPEN_BANKING_TAN);
        return jsonWebSignature;
    }
}
