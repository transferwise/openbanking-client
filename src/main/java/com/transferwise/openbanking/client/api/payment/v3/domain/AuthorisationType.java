package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthorisationType {

    ANY("Any"),
    SINGLE("Single");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
