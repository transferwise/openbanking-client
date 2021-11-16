package com.transferwise.openbanking.client.test.factory;

import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.oauth.domain.Scope;

import java.util.List;

public class SoftwareStatementDetailsFactory {

    public static SoftwareStatementDetails aSoftwareStatementDetails() {
        return SoftwareStatementDetails.builder()
            .softwareStatementId("software-statement-id")
            .permissions(List.of(Scope.OPENID, Scope.PAYMENTS))
            .redirectUrls(List.of("https://tpp.co.uk/1", "https://tpp.co.uk/2"))
            .build();
    }
}
