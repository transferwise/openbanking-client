package com.transferwise.openbanking.client.api.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationContext {
    /**
     * The authorization code returned by the ASPSP, as a result of a successful authorisation, which will be exchanged
     * for an access token.
     */
    private String authorizationCode;
    /**
     * The TPP URL that was specified in the authorization request to the ASPSP.
     */
    private String redirectUrl;
}
