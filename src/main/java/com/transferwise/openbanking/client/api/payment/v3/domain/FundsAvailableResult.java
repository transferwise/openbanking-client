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
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundsAvailableResult {

    @JsonProperty("FundsAvailableDateTime")
    private String fundsAvailableDateTime;

    @JsonProperty("FundsAvailable")
    private Boolean fundsAvailable;
}
