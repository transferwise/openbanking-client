package com.transferwise.openbanking.client.oauth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("token_type")
    private String tokenType;

    /**
     * The lifetime in seconds of the access token. Note, this is an optional field and may not be returned by all
     * servers.
     */
    @JsonProperty("expires_in")
    private Long expiresIn;
}
