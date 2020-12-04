package com.transferwise.openbanking.client.test;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestAspspDetails implements AspspDetails {
    private String internalId;
    private String financialId;
    private String apiBaseUrl;
    private String tokenUrl;
    private String registrationUrl;
    private String tokenIssuerUrl;
    private String tppRedirectUrl;
    private String clientId;
    private String clientSecret;
    private String signingAlgorithm;
    private String signingKeyId;
    private String paymentApiMinorVersion;
    private boolean registrationUsesJoseContentType;
    private boolean detachedSignatureUsesDirectoryIssFormat;

    @Override
    public String getApiBaseUrl(String majorVersion, String resource) {
        return apiBaseUrl;
    }

    @Override
    public boolean registrationUsesJoseContentType() {
        return registrationUsesJoseContentType;
    }

    @Override
    public boolean detachedSignatureUsesDirectoryIssFormat() {
        return detachedSignatureUsesDirectoryIssFormat;
    }
}
