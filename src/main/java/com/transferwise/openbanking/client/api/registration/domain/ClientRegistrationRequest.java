package com.transferwise.openbanking.client.api.registration.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.transferwise.openbanking.client.oauth.domain.GrantType;
import com.transferwise.openbanking.client.oauth.domain.ResponseType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Data structure for the request to an ASPSP for the dynamic client registration endpoint.
 *
 * @see <a href="https://openbankinguk.github.io/dcr-docs-pub/v3.2/dynamic-client-registration.html#obclientregistrationrequest1">API docs</a>
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientRegistrationRequest {

    private String iss;

    private Long iat;

    private Long exp;

    private String aud;

    private String jti;

    private String clientId;

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
