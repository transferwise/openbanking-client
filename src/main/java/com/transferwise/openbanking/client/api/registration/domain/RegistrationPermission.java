package com.transferwise.openbanking.client.api.registration.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RegistrationPermission {
    ACCOUNTS("accounts"),
    PAYMENTS("payments"),
    OPENID("openid");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
