package com.transferwise.openbanking.client.configuration;

import org.jose4j.jws.AlgorithmIdentifiers;

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
     * Get the identifier, assigned by a central service, of the ASPSP.
     *
     * <p>This value cannot be considered as a unique ASPSP identifier as the same value may be used across several banks
     * within the same group.
     *
     * <p>This value also cannot be considered as a human friendly identifier, as values are randomly assigned and have
     * no correlation to the ASPSP name.
     *
     * @return the financial identifier for the ASPSP
     */
    String getFinancialId();

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
     * <p>Not all banks support a registration API, therefore not all implementations implement this method.
     *
     * @return the client registration URL
     */
    default String getRegistrationUrl() {
        throw new UnsupportedOperationException("getRegistrationUrl not implemented");
    }

    /**
     * Get the URL to use as the intended audience value for a JWT generated for requesting an OAuth access token.
     *
     * <p>This URL is only applicable for certain client authentication methods, namely
     * {@link com.transferwise.openbanking.client.oauth.ClientAuthenticationMethod#PRIVATE_KEY_JWT}, therefore not all
     * implementations implement this method.
     *
     * @return the JWT intended audience URL
     */
    default String getTokenIssuerUrl() {
        throw new UnsupportedOperationException("getTokenIssuerUrl not implemented");
    }

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
     * Get the minor version of the payments API that should be used when making calls to the ASPSP payments API.
     *
     * @return the payments API minor version to use, defaults to 1
     */
    default String getPaymentApiMinorVersion() {
        return "1";
    }
}
