package com.transferwise.openbanking.client.api.payment.v1;

import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;

public interface IdempotencyKeyGenerator {
    String generateIdempotencyKey(SetupPaymentRequest setupPaymentRequest);
    String generateIdempotencyKey(SubmitPaymentRequest submitPaymentRequest);
}
