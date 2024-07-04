package com.transferwise.openbanking.client.oauth;

import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_TOKEN_LOG;

import com.transferwise.openbanking.client.api.common.ExceptionUtils;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.FapiHeaders;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import wiremock.org.apache.commons.lang3.Validate;

@RequiredArgsConstructor
@Slf4j
public class AsyncOAuthClient implements OAuthClient {

    private final ClientAuthentication clientAuthentication;
    private final WebClient webClient;

    @Override
    public AccessTokenResponse getAccessToken(GetAccessTokenRequest getAccessTokenRequest, AspspDetails aspspDetails) {
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

        return webClient.post()
            .uri(aspspDetails.getTokenUrl())
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .retrieve()
            .bodyToMono(AccessTokenResponse.class)
            .doOnSuccess(this::validateResponse)
            .onErrorResume(
                WebClientResponseException.class,
                e -> ExceptionUtils.handleWebClientResponseException(e, ON_ERROR_TOKEN_LOG)
            )
            .onErrorResume(WebClientException.class, e -> ExceptionUtils.handleWebClientException(e, ON_ERROR_TOKEN_LOG))
            .block();
    }

    private String encodeForm(Map<String, String> form) {
        List<NameValuePair> nameValuePairs = new ArrayList<>(form.size());

        for (Map.Entry<String, String> entry : form.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return URLEncodedUtils.format(nameValuePairs, StandardCharsets.UTF_8.name());
    }

    private void validateResponse(AccessTokenResponse response) {
        if (response == null || response.getAccessToken() == null || response.getAccessToken().isBlank()) {
            throw new ApiCallException("Empty or partial access token response returned " + response);
        }
    }
}
