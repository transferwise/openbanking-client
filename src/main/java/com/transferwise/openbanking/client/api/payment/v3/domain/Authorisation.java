package com.transferwise.openbanking.client.api.payment.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Authorisation {

    @JsonProperty("AuthorisationType")
    private AuthorisationType authorisationType;

    // TODO: add CompletionDateTime field
}
