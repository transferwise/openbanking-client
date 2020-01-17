package com.transferwise.openbanking.client.api.payment.v1.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transferwise.openbanking.client.api.payment.common.domain.InstructedAmount;
import com.transferwise.openbanking.client.api.payment.common.domain.RemittanceInformation;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Initiation {

    @JsonProperty("InstructionIdentification")
    private String instructionIdentification;

    @JsonProperty("EndToEndIdentification")
    private String endToEndIdentification;

    @JsonProperty("InstructedAmount")
    private InstructedAmount instructedAmount;

    @JsonProperty("CreditorAccount")
    private CreditorAccount creditorAccount;

    @JsonProperty("RemittanceInformation")
    private RemittanceInformation remittanceInformation;
}
