package com.transferwise.openbanking.client.api.payment.v1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
// TODO: replace this with a full definition of the fields
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSubmissionResponseData {

    @JsonProperty("PaymentSubmissionId")
    private String paymentSubmissionId;

    @JsonProperty("PaymentId")
    private String paymentId;

    @JsonProperty("Status")
    private String status;
}
