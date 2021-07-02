package com.transferwise.openbanking.client.test;

import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.oauth.ClientAuthenticationMethod;
import com.transferwise.openbanking.client.oauth.domain.GrantType;
import com.transferwise.openbanking.client.oauth.domain.ResponseType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class TestAspspDetails implements AspspDetails {
    private String internalId;
    private String organisationId;
    private String apiBaseUrl;
    private String tokenUrl;
    private String registrationUrl;
    private String registrationAudience;
    private String registrationIssuer;
    private Set<Scope> registrationAuthenticationScopes;
    private ClientAuthenticationMethod clientAuthenticationMethod;
    private String clientId;
    private String clientSecret;
    private String privateKeyJwtAuthenticationAudience;
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
    public String getRegistrationIssuer(SoftwareStatementDetails softwareStatementDetails) {
        return registrationIssuer;
    }

    @Override
    public Set<Scope> getRegistrationAuthenticationScopes(SoftwareStatementDetails softwareStatementDetails) {
        return registrationAuthenticationScopes;
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
