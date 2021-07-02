package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.api.common.BaseClient;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.ErrorResponse;
import com.transferwise.openbanking.client.oauth.domain.FapiHeaders;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RestOAuthClient extends BaseClient implements OAuthClient {

    private final ClientAuthentication clientAuthentication;

    protected RestOAuthClient(RestOperations restOperations,
                              JsonConverter jsonConverter,
                              ClientAuthentication clientAuthentication) {
        super(restOperations, jsonConverter);
        this.clientAuthentication = clientAuthentication;
    }

    @Override
    public ApiResponse<AccessTokenResponse, ErrorResponse> getAccessToken(GetAccessTokenRequest getAccessTokenRequest,
                                                                          AspspDetails aspspDetails) {

        clientAuthentication.addClientAuthentication(getAccessTokenRequest, aspspDetails);

        FapiHeaders requestHeaders = getAccessTokenRequest.getRequestHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // If we provide the body as a MultiValueMap to HttpEntity, then when FormHttpMessageConverter writes the map
        // as the request body, it also changes the content type header to include the charset (UTF-8). Certain ASPSPs
        // do not support the charset being included however, so we instead convert the map to a string ourselves to
        // avoid this happening.
        Map<String, String> requestBody = getAccessTokenRequest.getRequestBody();
        String encodedRequestBody = encodeForm(requestBody);

        HttpEntity<String> request = new HttpEntity<>(encodedRequestBody, requestHeaders);

        log.info("Requesting access token with grant type {} and interaction ID {}",
            requestBody.get("grant_type"),
            requestHeaders.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(aspspDetails.getTokenUrl(), HttpMethod.POST, request, String.class);
        } catch (RestClientResponseException e) {
            return mapClientExceptionWithResponse(e, ErrorResponse.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        AccessTokenResponse accessTokenResponse = jsonConverter.readValue(response.getBody(),
            AccessTokenResponse.class);
        if (isResponseInvalid(accessTokenResponse)) {
            return mapInvalidResponse(response);
        }

        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), accessTokenResponse);
    }

    private String encodeForm(Map<String, String> form) {
        List<NameValuePair> nameValuePairs = new ArrayList<>(form.size());

        for (Map.Entry<String, String> entry : form.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return URLEncodedUtils.format(nameValuePairs, StandardCharsets.UTF_8.name());
    }

    private boolean isResponseInvalid(AccessTokenResponse response) {
        return response == null || response.getAccessToken() == null || response.getAccessToken().isBlank();
    }
}
