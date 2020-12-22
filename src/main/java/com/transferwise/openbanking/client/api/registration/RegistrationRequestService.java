package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ApplicationType;
import com.transferwise.openbanking.client.api.registration.domain.RegistrationPermission;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.TppConfiguration;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegistrationRequestService {

    private static final long REGISTRATION_TOKEN_VALIDITY_SECONDS = 300;

    private final KeySupplier keySupplier;
    private final TppConfiguration tppConfiguration;

    public ClientRegistrationRequest generateRegistrationRequest(String softwareStatement, AspspDetails aspspDetails) {
        Instant now = Instant.now();

        ClientAuthenticationMethod clientAuthenticationMethod = aspspDetails.getClientAuthenticationMethod();
        String signingAlgorithm = aspspDetails.getSigningAlgorithm();

        ClientRegistrationRequest.ClientRegistrationRequestBuilder requestBuilder = ClientRegistrationRequest.builder()
            .iss(tppConfiguration.getSoftwareStatementId())
            .aud(aspspDetails.getRegistrationIssuerUrl())
            .iat(now.getEpochSecond())
            .exp(now.plusSeconds(REGISTRATION_TOKEN_VALIDITY_SECONDS).getEpochSecond())
            .jti(generateJwtIdValue(aspspDetails))
            .applicationType(ApplicationType.WEB)
            .scope(generateScopeValue())
            .softwareId(tppConfiguration.getSoftwareStatementId())
            .softwareStatement(softwareStatement)
            .grantTypes(aspspDetails.getGrantTypes())
            .responseTypes(aspspDetails.getResponseTypes())
            .tokenEndpointAuthMethod(clientAuthenticationMethod.getMethodName())
            .idTokenSignedResponseAlg(signingAlgorithm)
            .requestObjectSigningAlg(signingAlgorithm)
            .redirectUris(List.of(tppConfiguration.getRedirectUrl()));

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

    private String generateScopeValue() {
        List<RegistrationPermission> permissions;
        // registration requests always have to include the OPENID permission
        if (tppConfiguration.getPermissions().contains(RegistrationPermission.OPENID)) {
            permissions = tppConfiguration.getPermissions();
        } else {
            permissions = new ArrayList<>();
            permissions.add(RegistrationPermission.OPENID);
            permissions.addAll(tppConfiguration.getPermissions());
        }

        return permissions.stream()
            .map(RegistrationPermission::getValue)
            .collect(Collectors.joining(" "));
    }

    private String getTransportCertificateSubjectName(AspspDetails aspspDetails) {
        Certificate transportCertificate = keySupplier.getTransportCertificate(aspspDetails);
        if (transportCertificate instanceof X509Certificate) {
            return ((X509Certificate) transportCertificate).getSubjectX500Principal().getName();
        } else {
            throw new ClientException("Supplied transport certificate is not an X509 certificate, cannot determine " +
                "transport certificate subject name");
        }
    }
}
