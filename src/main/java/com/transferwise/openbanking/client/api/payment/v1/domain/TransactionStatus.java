package com.transferwise.openbanking.client.api.payment.v1.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    ACCEPTED_CUSTOMER_PROFILE("AcceptedCustomerProfile"),
    ACCEPTED_SETTLEMENT_COMPLETED("AcceptedSettlementCompleted"),
    ACCEPTED_SETTLEMENT_IN_PROCESS("AcceptedSettlementInProcess"),
    ACCEPTED_TECHNICAL_VALIDATION("AcceptedTechnicalValidation"),
    PENDING("Pending"),
    REJECTED("Rejected");

    private final String value;

    public static Optional<TransactionStatus> fromValue(String value) {
        for (TransactionStatus transactionStatus : TransactionStatus.values()) {
            if (transactionStatus.getValue().equals(value)) {
                return Optional.of(transactionStatus);
            }
        }
        return Optional.empty();
    }
}
