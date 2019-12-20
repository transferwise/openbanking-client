package com.transferwise.openbanking.client.api.payment.v1.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transferwise.openbanking.client.api.payment.common.domain.Initiation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubmitPaymentRequestData extends SetupPaymentRequestData {

    public SubmitPaymentRequestData(Initiation initiation, String paymentId) {
        super(initiation);
        this.paymentId = paymentId;
    }

    @JsonProperty("PaymentId")
    private String paymentId;
}
