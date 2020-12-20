package com.transferwise.openbanking.client.oauth.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GrantType {

    AUTHORIZATION_CODE("authorization_code"),
    CLIENT_CREDENTIALS("client_credentials"),
    REFRESH_TOKEN("refresh_token");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
