package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: replace this with a full definition of the fields
@JsonIgnoreProperties(ignoreUnknown = true)
public class DomesticPaymentConsentResponse {

    @JsonProperty("Data")
    private DomesticPaymentConsentResponseData data;
}
