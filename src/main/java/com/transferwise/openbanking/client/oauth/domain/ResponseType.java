package com.transferwise.openbanking.client.oauth.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResponseType {

    CODE("code"),
    CODE_AND_ID_TOKEN("code id_token");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
