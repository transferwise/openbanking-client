package com.transferwise.openbanking.client.api.registration.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApplicationType {
    WEB("web"),
    MOBILE("mobile");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
