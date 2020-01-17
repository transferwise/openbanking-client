package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DomesticPaymentConsentData {

    @JsonProperty("Initiation")
    private Initiation initiation;

    @JsonProperty("Authorisation")
    private Authorisation authorisation;

    // TODO: add SCASupportedData field
}
