package com.transferwise.openbanking.client.oauth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data structure for the structured error response from an ASPSP for the OAuth access token endpoint.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-5.2">API docs</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ErrorResponse {

    private String error;
    private String errorDescription;
    private String errorUri;
}
