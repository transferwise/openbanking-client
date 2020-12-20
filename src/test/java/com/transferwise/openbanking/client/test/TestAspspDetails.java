package com.transferwise.openbanking.client.test;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.oauth.ClientAuthenticationMethod;
import com.transferwise.openbanking.client.oauth.domain.GrantType;
import com.transferwise.openbanking.client.oauth.domain.ResponseType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestAspspDetails implements AspspDetails {
    private String internalId;
    private String financialId;
    private String apiBaseUrl;
    private String tokenUrl;
    private String registrationUrl;
    private String registrationIssuerUrl;
    private String tokenIssuerUrl;
    private String tppRedirectUrl;
    private ClientAuthenticationMethod clientAuthenticationMethod;
    private String clientId;
    private String clientSecret;
    private String signingAlgorithm;
    private String signingKeyId;
    private List<GrantType> grantTypes;
    private List<ResponseType> responseTypes;
    private String paymentApiMinorVersion;
    private boolean registrationUsesJoseContentType;
    private boolean detachedSignatureUsesDirectoryIssFormat;
    private boolean registrationRequiresLowerCaseJtiClaim;

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

    @Override
    public boolean registrationRequiresLowerCaseJtiClaim() {
        return registrationRequiresLowerCaseJtiClaim;
    }
}
