package com.transferwise.openbanking.client.api.registration.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data structure for the structured error response from an ASPSP for the dynamic client registration endpoint.
 *
 * @see <a href="https://openbankinguk.github.io/dcr-docs-pub/v3.2/dynamic-client-registration.html#error-structure">API docs</a>
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
}
