package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ApplicationType;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.RegistrationPermission;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.TppConfiguration;
import com.transferwise.openbanking.client.oauth.ClientAuthenticationMethod;
import com.transferwise.openbanking.client.oauth.domain.GrantType;
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

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RegistrationRequestServiceTest {

    private static final String JTI_UPPERCASE_REGEX =
        "^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$";
    private static final String JTI_LOWERCASE_REGEX =
        "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    @Mock
    private KeySupplier keySupplier;

    private TppConfiguration tppConfiguration;

    private RegistrationRequestService registrationRequestService;

    @BeforeEach
    void init() {
        tppConfiguration = aTppConfiguration();

        registrationRequestService = new RegistrationRequestService(keySupplier, tppConfiguration);
    }

    @Test
    void generateRegistrationRequest() {
        String softwareStatement = "software-statement";
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationIssuerUrl("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(false)
            .build();

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
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
        Assertions.assertEquals(aspspDetails.getRegistrationIssuerUrl(), clientRegistrationRequest.getAud());
        Assertions.assertEquals(aspspDetails.getGrantTypes(), clientRegistrationRequest.getGrantTypes());
        Assertions.assertEquals(aspspDetails.getClientAuthenticationMethod().getMethodName(),
            clientRegistrationRequest.getTokenEndpointAuthMethod());
        Assertions.assertEquals(aspspDetails.getSigningAlgorithm(),
            clientRegistrationRequest.getIdTokenSignedResponseAlg());
        Assertions.assertEquals(aspspDetails.getSigningAlgorithm(),
            clientRegistrationRequest.getRequestObjectSigningAlg());
        Assertions.assertEquals(tppConfiguration.getSoftwareStatementId(), clientRegistrationRequest.getIss());
        Assertions.assertEquals(tppConfiguration.getSoftwareStatementId(), clientRegistrationRequest.getSoftwareId());
        Assertions.assertEquals(List.of(tppConfiguration.getRedirectUrl()),
            clientRegistrationRequest.getRedirectUris());
    }

    @Test
    void generateRegistrationRequestUsesLowerCaseJtiIfAspspRequiresIt() {
        String softwareStatement = "software-statement";
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationIssuerUrl("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(true)
            .build();

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            aspspDetails);

        Assertions.assertTrue(clientRegistrationRequest.getJti().matches(JTI_LOWERCASE_REGEX));
    }

    @Test
    void generateRegistrationRequestAlwaysIncludesOpenIdPermissionInScopes() {
        String softwareStatement = "software-statement";
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationIssuerUrl("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(false)
            .build();
        tppConfiguration.setPermissions(List.of(RegistrationPermission.PAYMENTS));

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            aspspDetails);

        Assertions.assertEquals("openid payments", clientRegistrationRequest.getScope());
    }

    @Test
    void generateRegistrationRequestSetsClaimsWhenAspspUsesTlsClientAuth() throws Exception {
        String softwareStatement = "software-statement";
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationIssuerUrl("registration-issuer-url")
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
            aspspDetails);

        Assertions.assertEquals(transportCertificate.getSubjectX500Principal().getName(),
            clientRegistrationRequest.getTlsClientAuthSubjectDn());
    }

    @Test
    void generateRegistrationRequestSetsClaimsWhenAspspUsesPrivateKeyJwtClientAuth() {
        String softwareStatement = "software-statement";
        AspspDetails aspspDetails = TestAspspDetails.builder()
            .registrationIssuerUrl("registration-issuer-url")
            .grantTypes(List.of(GrantType.CLIENT_CREDENTIALS, GrantType.AUTHORIZATION_CODE))
            .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .signingAlgorithm(AlgorithmIdentifiers.RSA_PSS_USING_SHA256)
            .registrationRequiresLowerCaseJtiClaim(false)
            .build();

        ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
            softwareStatement,
            aspspDetails);

        Assertions.assertEquals(aspspDetails.getSigningAlgorithm(),
            clientRegistrationRequest.getTokenEndpointAuthSigningAlg());
    }

    private TppConfiguration aTppConfiguration() {
        return TppConfiguration.builder()
            .softwareStatementId("software-statement-id")
            .permissions(List.of(RegistrationPermission.OPENID, RegistrationPermission.PAYMENTS))
            .redirectUrl("tpp-redirect-url")
            .build();
    }
}
