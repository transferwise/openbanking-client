package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.aspsp.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;

public interface ClientAuthentication {

    ClientAuthenticationMethod getSupportedMethod();

    void addClientAuthentication(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails);
}
