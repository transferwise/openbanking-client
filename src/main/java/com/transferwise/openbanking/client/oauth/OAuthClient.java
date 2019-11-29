package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.aspsp.AspspDetails;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;

public interface OAuthClient {

    AccessTokenResponse getAccessToken(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails);
}
