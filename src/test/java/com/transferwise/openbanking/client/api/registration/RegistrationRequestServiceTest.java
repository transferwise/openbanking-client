package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ApplicationType;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.oauth.ClientAuthenticationMethod;
import com.transferwise.openbanking.client.oauth.domain.GrantType;
import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.security.KeySupplier;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import com.transferwise.openbanking.client.test.TestKeyUtils;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.cert.X509Certificate;
import java.util.List;

import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RegistrationRequestServiceTest {

    private static final String JTI_UPPERCASE_REGEX =
        "^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$";
    private static final String JTI_LOWERCASE_REGEX =
        "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    @Mock
    private KeySupplier keySupplier;

    private RegistrationRequestService registrationRequestService;

    @BeforeEach
    void init() {
        registrationRequestService = new RegistrationRequestService(keySupplier);
    }

    @Test
    void generateRegistrationRequest() {
        String softwareStatement = "software-statement";
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationAudience("registration-issuer-url")
            .registrationIssuer("organisation-id")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(false)
            .build();

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            softwareStatementDetails,
            aspspDetails);

        Assertions.assertTrue(clientRegistrationRequest.getIat() > 0);
        Assertions.assertTrue(clientRegistrationRequest.getExp() > 0);
        Assertions.assertTrue(clientRegistrationRequest.getExp() > clientRegistrationRequest.getIat());
        Assertions.assertNull(clientRegistrationRequest.getTlsClientAuthSubjectDn());
        Assertions.assertNull(clientRegistrationRequest.getTokenEndpointAuthSigningAlg());
        Assertions.assertTrue(clientRegistrationRequest.getJti().matches(JTI_UPPERCASE_REGEX));
        Assertions.assertEquals("openid payments", clientRegistrationRequest.getScope());
        Assertions.assertEquals(ApplicationType.WEB, clientRegistrationRequest.getApplicationType());
        Assertions.assertEquals(softwareStatement, clientRegistrationRequest.getSoftwareStatement());
        Assertions.assertEquals(aspspDetails.getRegistrationAudience(), clientRegistrationRequest.getAud());
        Assertions.assertEquals(aspspDetails.getGrantTypes(), clientRegistrationRequest.getGrantTypes());
        Assertions.assertEquals(aspspDetails.getClientAuthenticationMethod().getMethodName(),
            clientRegistrationRequest.getTokenEndpointAuthMethod());
        Assertions.assertEquals(aspspDetails.getSigningAlgorithm(),
            clientRegistrationRequest.getIdTokenSignedResponseAlg());
        Assertions.assertEquals(aspspDetails.getSigningAlgorithm(),
            clientRegistrationRequest.getRequestObjectSigningAlg());
        Assertions.assertEquals("organisation-id", clientRegistrationRequest.getIss());
        Assertions.assertEquals(softwareStatementDetails.getSoftwareStatementId(),
            clientRegistrationRequest.getSoftwareId());
        Assertions.assertEquals(softwareStatementDetails.getRedirectUrls(),
            clientRegistrationRequest.getRedirectUris());
    }

    @Test
    void generateRegistrationRequestUsesLowerCaseJtiIfAspspRequiresIt() {
        String softwareStatement = "software-statement";
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationAudience("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(true)
            .build();

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            softwareStatementDetails,
            aspspDetails);

        Assertions.assertTrue(clientRegistrationRequest.getJti().matches(JTI_LOWERCASE_REGEX));
    }

    @Test
    void generateRegistrationRequestAlwaysIncludesOpenIdPermissionInScopes() {
        String softwareStatement = "software-statement";
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        softwareStatementDetails.setPermissions(List.of(Scope.PAYMENTS));
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationAudience("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(false)
            .build();

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            softwareStatementDetails,
            aspspDetails);

        Assertions.assertEquals("openid payments", clientRegistrationRequest.getScope());
    }

    @Test
    void generateRegistrationRequestSetsClaimsWhenAspspUsesTlsClientAuth() throws Exception {
        String softwareStatement = "software-statement";
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationAudience("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.TLS_CLIENT_AUTH)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(false)
            .build();

        X509Certificate transportCertificate = TestKeyUtils.aCertificate();
        Mockito.when(keySupplier.getTransportCertificate(Mockito.eq(aspspDetails)))
            .thenReturn(transportCertificate);

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            softwareStatementDetails,
            aspspDetails);

        Assertions.assertEquals(transportCertificate.getSubjectX500Principal().getName(),
            clientRegistrationRequest.getTlsClientAuthSubjectDn());
    }

    @Test
    void generateRegistrationRequestSetsClaimsWhenAspspUsesPrivateKeyJwtClientAuth() {
        String softwareStatement = "software-statement";
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationAudience("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(false)
            .build();

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            softwareStatementDetails,
            aspspDetails);

        Assertions.assertEquals(aspspDetails.getSigningAlgorithm(),
            clientRegistrationRequest.getTokenEndpointAuthSigningAlg());
    }
}
