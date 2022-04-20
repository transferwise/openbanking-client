package com.transferwise.openbanking.client.configuration;

import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.oauth.ClientAuthenticationMethod;
import com.transferwise.openbanking.client.oauth.domain.GrantType;
import com.transferwise.openbanking.client.oauth.domain.ResponseType;
import org.jose4j.jws.AlgorithmIdentifiers;

import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines the integration details with a specific ASPSP.
 */
public interface AspspDetails {

    /**
     * Get the identifier of the ASPSP, which is intended for internal use, to differentiate between interface
     * implementations.
     *
     * <p>Values should be unique across all implementation of this interface.
     *
     * @return the internal ASPSP identifier
     */
    String getInternalId();

    /**
     * Get the identifier, assigned by the Open Banking directory, for the ASPSP organisation.
     *
     * <p>This value cannot be considered as a unique ASPSP identifier as the same value may be used across several
     * ASPSP brands within the same organisation.
     *
     * <p>This value also cannot be considered as a human friendly identifier, as values are randomly assigned and have
     * no correlation to the ASPSP name.
     *
     * @return the organisation identifier for the ASPSP
     */
    String getOrganisationId();

    /**
     * Get the base URL for the ASPSPs Open Banking API, to use as the prefix for an API call to the ASPSP.
     *
     * @param majorVersion The major version of the API that will be called
     * @param resource The resource within the API that will be called
     * @return the API base URL
     */
    String getApiBaseUrl(String majorVersion, String resource);

    /**
     * Get the URL the ASPSP exposes for requesting a OAuth access token.
     *
     * @return the OAuth token URL
     */
    String getTokenUrl();

    /**
     * Get the URL the ASPSP exposes for registering this service as a client with the ASPSP.
     *
     * <p>Not all ASPSPs support a registration API, therefore not all implementations implement this method.
     *
     * @return the client registration URL
     */
    default String getRegistrationUrl() {
        throw new UnsupportedOperationException("getRegistrationUrl not implemented");
    }

    /**
     * Get the value to use as the intended audience claim within a client registration request. For most ASPSPs this
     * will be ASPSP organisation ID (within the Open Banking directory), however some ASPSPs may require a different
     * value.
     *
     * <p>Not all ASPSPs support a registration API, therefore not all implementations implement this method.
     *
     * @return the intended audience claim value to use
     */
    default String getRegistrationAudience() {
        return getOrganisationId();
    }

    /**
     * Get the value to use as the issuer claim within a client registration request. For most ASPSPs this will be the
     * ID of the software statement used for the registration, however some ASPSPs may require a different value.
     *
     * @param softwareStatementDetails the details of the software statement being used for the registration
     * @return the issuer claim value to use, defaults to the software statement ID
     */
    default String getRegistrationIssuer(SoftwareStatementDetails softwareStatementDetails) {
        return softwareStatementDetails.getSoftwareStatementId();
    }

    /**
     * Get the set of scopes to request for when obtaining an access token, to use for an authenticated get / update /
     * delete client registration API call.
     *
     * <p>Some ASPSPs require the set of requested scopes to not contain the {@link Scope#OPENID}
     * scope, some require it to contain the {@link Scope#OPENID} scope, and some require no scopes to
     * be requested at all.
     *
     * <p>By default this returns {@link Scope#OPENID} plus the permissions in
     * {@link SoftwareStatementDetails#permissions}.
     *
     * @param softwareStatementDetails the details of the software statement being used for the registration
     * @return The set of scopes to request an access token with
     */
    default Set<Scope> getRegistrationAuthenticationScopes(SoftwareStatementDetails softwareStatementDetails) {
        Set<Scope> permissions = new LinkedHashSet<>();
        permissions.add(Scope.OPENID);
        // As we request a scope of what the permissions the software statement details currently has, we don't really
        // support updating the permissions of a client registration, but as this can't be modified in the Open Banking
        // directory this shouldn't be an issue.
        permissions.addAll(softwareStatementDetails.getPermissions());
        return permissions;
    }

    /**
     * Get the subject distinguished name of the given transport certificate, to use as the subject distinguished name
     * claim in a registration request, when using TLS client authentication.
     *
     * <p>Some ASPSPs require this value to be in a specific format.
     *
     * <p>By default this returns the certificate subject distinguished name, in the RFC 2253 format.
     *
     * @param certificate the transport certificate being used for the client registration request
     * @return the certificate subject name
     */
    default String getRegistrationTransportCertificateSubjectName(X509Certificate certificate) {
        return certificate.getSubjectX500Principal().getName(X500Principal.RFC2253);
    }

    /**
     * Get the authentication method to use for identifying ourselves as a client to the ASPSP.
     *
     * @return the client authentication method to use
     */
    ClientAuthenticationMethod getClientAuthenticationMethod();

    /**
     * Get the ID value to use for identifying ourselves as a client to the ASPSP.
     *
     * <p>Normally this is provided by the ASPSP as a result of registering with the ASPSP.
     *
     * @return the client identifier
     */
    String getClientId();

    /**
     * Get the secret (password) to use for identifying ourselves as a client to the ASPSP.
     *
     * <p>Normally this is provided by the ASPSP as a result of registering with the ASPSP.
     *
     * <p>A client secret is only applicable for certain client authentication methods, therefore not all
     * implementations implement this method.
     *
     * @return the client secret
     */
    default String getClientSecret() {
        throw new UnsupportedOperationException("getClientSecret not implemented");
    }

    /**
     * Get the value to use as the intended audience claim, in the JWT generated for requesting an OAuth access token,
     * when using the private key JWT client authentication method.
     *
     * @return the JWT intended audience claim value to use, defaults to the token URL
     */
    default String getPrivateKeyJwtAuthenticationAudience() {
        return getTokenUrl();
    }

    /**
     * Get the JWT signing algorithm this ASPSP supports, for JWT values sent <b>to</b> the ASPSP.
     *
     * <p>Implementations should return a value supported by the jose4j library, which is a value contained in
     * {@link org.jose4j.jws.AlgorithmIdentifiers}.
     *
     * @return the supported JWT signing algorithm, by default PS256
     */
    default String getSigningAlgorithm() {
        return AlgorithmIdentifiers.RSA_PSS_USING_SHA256;
    }

    /**
     * Get the ID of the key, within the Open Banking directory, to use for signing data to send to ASPSPs.
     *
     * @return the signing key ID.
     */
    String getSigningKeyId();

    /**
     * Get the OAuth grant types, that the ASPSP supports and the TPP may request, to specify to the ASPSP during
     * registration.
     *
     * @return the grant types to specify, defaults to all types
     */
    default List<GrantType> getGrantTypes() {
        return List.of(GrantType.values());
    }

    /**
     * Get the OAUth response types, that the ASPSP supports and the TPP may request, to specify to the ASPSP during
     * registration.
     *
     * @return the response types to specify, defaults to <code>CODE_AND_ID_TOKEN</code>
     */
    default List<ResponseType> getResponseTypes() {
        return List.of(ResponseType.CODE_AND_ID_TOKEN);
    }

    /**
     * Get the minor version of the payments API that should be used when making calls to the ASPSP payments API.
     *
     * @return the payments API minor version to use, defaults to 1
     */
    default String getPaymentApiMinorVersion() {
        return "1";
    }

    /**
     * Whether or not the ASPSP expects client registration requests to use the 'application/jose' content type, rather
     * than the 'application/jwt' content type.
     *
     * @return <code>true</code> if the jose content type should be used, <code>false</code> otherwise
     */
    default boolean registrationUsesJoseContentType() {
        return false;
    }

    /**
     * Whether or not the ASPSP expects detached message signatures to contain the 'b64' header.
     *
     * @return <code>true</code> if the b64 header should be included, <code>false</code> otherwise
     */
    default boolean detachedSignaturesRequireB64Header() {
        return true;
    }

    /**
     * Whether or not the ASPSP expects detached message signatures to use the Open Banking directory format for the
     * 'http://openbanking.org.uk/iss' claim, i.e. '{{org-id}}/{{software-statement-id}}', or to use the signing
     * certificate subject value.
     *
     * @return <code>true</code> if the detached signature should use the Open Banking directory format for the ISS
     * claim, <code>false</code> otherwise
     */
    default boolean detachedSignatureUsesDirectoryIssFormat() {
        return true;
    }

    /**
     * Whether or not the ASPSP requires the JWT ID (jti) claim, within client registration requests, to use only lower
     * case characters.
     *
     * <p>This is false by default, as Version 3 of the registration API specifies it should be an uppercase v4 UUID,
     * however some ASPSPs do not follow this requirement and instead require lowercase characters.
     *
     * @return <code>true</code> if the jti claim should contain only lowercase characters, <code>false</code> otherwise
     */
    default boolean registrationRequiresLowerCaseJtiClaim() {
        return false;
    }

    default String getVrpBaseResourceName() {
        return "pisp";
    }
}
