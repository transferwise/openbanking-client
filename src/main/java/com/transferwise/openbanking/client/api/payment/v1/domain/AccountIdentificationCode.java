package com.transferwise.openbanking.client.api.payment.v1.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountIdentificationCode {
    IBAN("IBAN"),
    SORT_CODE_ACCOUNT_NUMBER("SortCodeAccountNumber");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
