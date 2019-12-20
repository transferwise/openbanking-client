package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus {
    ACCEPTED_SETTLEMENT_COMPLETED("AcceptedSettlementCompleted"),
    ACCEPTED_SETTLEMENT_IN_PROCESS("AcceptedSettlementInProcess"),
    PENDING("Pending"),
    REJECTED("Rejected");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
