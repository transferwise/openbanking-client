package com.transferwise.openbanking.client.api.payment.v1.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transferwise.openbanking.client.api.payment.common.domain.Risk;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SetupPaymentRequest {

    @JsonProperty("Data")
    private SetupPaymentRequestData setupPaymentRequestData;

    @JsonProperty("Risk")
    private Risk risk;
}
