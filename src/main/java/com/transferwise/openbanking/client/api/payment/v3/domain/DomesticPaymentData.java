package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transferwise.openbanking.client.api.payment.common.domain.Initiation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DomesticPaymentData {

    @JsonProperty("ConsentId")
    private String consentId;

    @JsonProperty("Initiation")
    private Initiation initiation;
}
