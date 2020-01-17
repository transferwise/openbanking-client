package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountIdentificationCode {
    BBAN("UK.OBIE.BBAN"),
    IBAN("UK.OBIE.IBAN"),
    PAN("UK.OBIE.PAN"),
    PAYM("UK.OBIE.Paym"),
    SORT_CODE_ACCOUNT_NUMBER("UK.OBIE.SortCodeAccountNumber");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
