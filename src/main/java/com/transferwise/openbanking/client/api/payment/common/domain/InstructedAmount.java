package com.transferwise.openbanking.client.api.payment.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class InstructedAmount {

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("Currency")
    private String currency;
}
