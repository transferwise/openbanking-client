package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ApplicationType;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.oauth.ScopeFormatter;
import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ClientException;
import com.transferwise.openbanking.client.oauth.ClientAuthenticationMethod;
import com.transferwise.openbanking.client.security.KeySupplier;
import lombok.RequiredArgsConstructor;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RegistrationRequestService {

    private static final long REGISTRATION_TOKEN_VALIDITY_SECONDS = 300;

    private final KeySupplier keySupplier;

    /**
     * Generate the claims for a client registration request to a given ASPSP, which can then be signed and used as the
     * request body for calls to the ASPSP's client registration API.
     * <p>
     * The claims are generated based on the details defined in the supplied {@link SoftwareStatementDetails} and
     * {@link AspspDetails}.
     * <p>
     * The generated claims can then be further modified prior to being signed and sent to the ASPSP.
     *
     * @param softwareStatementAssertion The software statement assertion, issued by the Open Banking directory
     * @param softwareStatementDetails   The details of the software statement that the ASPSP will be registered against
     * @param aspspDetails               The details of the ASPSP, for which the registration claims will be sent to
     * @return The generated registration claims
     */
    public ClientRegistrationRequest generateRegistrationRequest(String softwareStatementAssertion,
                                                                 SoftwareStatementDetails softwareStatementDetails,
                                                                 AspspDetails aspspDetails) {
        Instant now = Instant.now();

        ClientAuthenticationMethod clientAuthenticationMethod = aspspDetails.getClientAuthenticationMethod();
        String signingAlgorithm = aspspDetails.getSigningAlgorithm();

        ClientRegistrationRequest.ClientRegistrationRequestBuilder requestBuilder = ClientRegistrationRequest.builder()
            .iss(aspspDetails.getRegistrationIssuer(softwareStatementDetails))
            .aud(aspspDetails.getRegistrationAudience())
            .iat(now.getEpochSecond())
            .exp(now.plusSeconds(REGISTRATION_TOKEN_VALIDITY_SECONDS).getEpochSecond())
            .jti(generateJwtIdValue(aspspDetails))
            .applicationType(ApplicationType.WEB)
            .scope(generateScopeValue(softwareStatementDetails))
            .softwareId(softwareStatementDetails.getSoftwareStatementId())
            .softwareStatement(softwareStatementAssertion)
            .grantTypes(aspspDetails.getGrantTypes())
            .responseTypes(aspspDetails.getResponseTypes())
            .tokenEndpointAuthMethod(clientAuthenticationMethod.getMethodName())
            .idTokenSignedResponseAlg(signingAlgorithm)
            .requestObjectSigningAlg(signingAlgorithm)
            .redirectUris(softwareStatementDetails.getRedirectUrls())
            .clientId(aspspDetails.getClientId());

        if (ClientAuthenticationMethod.TLS_CLIENT_AUTH == clientAuthenticationMethod) {
            requestBuilder.tlsClientAuthSubjectDn(getTransportCertificateSubjectName(aspspDetails));
        }

        if (ClientAuthenticationMethod.PRIVATE_KEY_JWT == clientAuthenticationMethod) {
            requestBuilder.tokenEndpointAuthSigningAlg(signingAlgorithm);
        }

        return requestBuilder.build();
    }

    private String generateJwtIdValue(AspspDetails aspspDetails) {
        String jti = UUID.randomUUID().toString();
        if (aspspDetails.registrationRequiresLowerCaseJtiClaim()) {
            return jti.toLowerCase();
        } else {
            return jti.toUpperCase();
        }
    }

    private String generateScopeValue(SoftwareStatementDetails softwareStatementDetails) {
        List<Scope> permissions;
        // registration requests always have to include the OPENID permission
        if (softwareStatementDetails.getPermissions().contains(Scope.OPENID)) {
            permissions = softwareStatementDetails.getPermissions();
        } else {
            permissions = new ArrayList<>();
            permissions.add(Scope.OPENID);
            permissions.addAll(softwareStatementDetails.getPermissions());
        }

        return ScopeFormatter.formatScopes(permissions);
    }

    private String getTransportCertificateSubjectName(AspspDetails aspspDetails) {
        Certificate transportCertificate = keySupplier.getTransportCertificate(aspspDetails);
        if (transportCertificate instanceof X509Certificate) {
            return aspspDetails.getRegistrationTransportCertificateSubjectName((X509Certificate) transportCertificate);
        } else {
            throw new ClientException("Supplied transport certificate is not an X509 certificate, cannot determine " +
                "transport certificate subject name");
        }
    }
}
