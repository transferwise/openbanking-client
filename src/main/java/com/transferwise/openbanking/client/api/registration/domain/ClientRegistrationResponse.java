package com.transferwise.openbanking.client.api.registration.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.transferwise.openbanking.client.oauth.domain.GrantType;
import com.transferwise.openbanking.client.oauth.domain.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data structure for the response from an ASPSP for the dynamic client registration endpoint.
 *
 * @see <a href="https://openbankinguk.github.io/dcr-docs-pub/v3.2/dynamic-client-registration.html#obclientregistrationrequest1">API docs</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientRegistrationResponse {

    private String clientId;

    private String clientSecret;

    // this should be an integer, containing seconds since epoch, but some ASPSPs incorrectly return a formatted
    // timestamp, so we use a string to keep things simple and avoid de-serialisation issues
    private String clientIdIssuedAt;

    // this should be an integer, containing seconds since epoch, but some ASPSPs incorrectly return a formatted
    // timestamp, so we use a string to keep things simple and avoid de-serialisation issues
    private String clientSecretExpiresAt;

    private List<String> redirectUris;

    private String tokenEndpointAuthMethod;

    private List<GrantType> grantTypes;

    private List<ResponseType> responseTypes;

    private String softwareId;

    private String scope;

    private String softwareStatement;

    private ApplicationType applicationType;

    private String idTokenSignedResponseAlg;

    private String requestObjectSigningAlg;

    private String tokenEndpointAuthSigningAlg;

    private String tlsClientAuthSubjectDn;
}
