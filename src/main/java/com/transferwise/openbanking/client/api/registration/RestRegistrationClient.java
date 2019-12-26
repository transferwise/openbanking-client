package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RequiredArgsConstructor
public class RestRegistrationClient implements RegistrationClient {

    private final RestOperations restTemplate;

    @Override
    public String registerClient(String signedClaims, AspspDetails aspspDetails) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/jwt"));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // as we're using a raw String as the body type, we need to manually set the header
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

        HttpEntity<String> request = new HttpEntity<>(signedClaims, headers);

        try {
            // TODO: use a proper object as the response, not just a raw string
            ResponseEntity<String> response = restTemplate.exchange(
                aspspDetails.getRegistrationUrl(),
                HttpMethod.POST,
                request,
                String.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to register client endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to register client endpoint failed, and no response body returned", e);
        }
    }
}
