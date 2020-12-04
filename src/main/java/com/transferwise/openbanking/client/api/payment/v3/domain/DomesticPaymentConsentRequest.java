package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transferwise.openbanking.client.api.payment.common.domain.Risk;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DomesticPaymentConsentRequest {

    @JsonProperty("Data")
    private DomesticPaymentConsentData data;

    @JsonProperty("Risk")
    private Risk risk;
}
