package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class RestRegistrationClient implements RegistrationClient {

    private final JwtClaimsSigner jwtClaimsSigner;
    private final OAuthClient oAuthClient;
    private final RestOperations restTemplate;

    @Override
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest clientRegistrationRequest,
                                                     AspspDetails aspspDetails) {

        HttpHeaders headers = new HttpHeaders();
        if (aspspDetails.registrationUsesJoseContentType()) {
            headers.setContentType(MediaType.valueOf("application/jose"));
        } else {
            headers.setContentType(MediaType.valueOf("application/jwt"));
        }
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        // as we're using a raw String as the body type, we need to manually set the header
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        String signedClaims = jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails);
        HttpEntity<String> request = new HttpEntity<>(signedClaims, headers);

        log.debug("Sending registration request to '{}' with headers '{}' and body '{}'",
            aspspDetails.getRegistrationUrl(),
            request.getHeaders(),
            request.getBody());

        try {
            ResponseEntity<ClientRegistrationResponse> response = restTemplate.exchange(
                aspspDetails.getRegistrationUrl(),
                HttpMethod.POST,
                request,
                ClientRegistrationResponse.class);

            log.debug("Received registration response with headers '{}' and body '{}'",
                response.getHeaders(),
                response.getBody());

            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to register client endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to register client endpoint failed, and no response body returned", e);
        }
    }

    @Override
    public ClientRegistrationResponse updateRegistration(ClientRegistrationRequest clientRegistrationRequest,
                                                         AspspDetails aspspDetails) {

        HttpHeaders headers = new HttpHeaders();
        if (aspspDetails.registrationUsesJoseContentType()) {
            headers.setContentType(MediaType.valueOf("application/jose"));
        } else {
            headers.setContentType(MediaType.valueOf("application/jwt"));
        }
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        // as we're using a raw String as the body type, we need to manually set the header
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(getClientCredentialsToken(aspspDetails));

        String signedClaims = jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails);
        HttpEntity<String> request = new HttpEntity<>(signedClaims, headers);

        log.debug("Sending update registration request to '{}' with headers '{}' and body '{}'",
            aspspDetails.getRegistrationUrl(),
            request.getHeaders(),
            request.getBody());

        try {
            ResponseEntity<ClientRegistrationResponse> response = restTemplate.exchange(
                aspspDetails.getRegistrationUrl() + "/{clientId}",
                HttpMethod.PUT,
                request,
                ClientRegistrationResponse.class,
                aspspDetails.getClientId());

            log.debug("Received update registration response with headers '{}' and body '{}'",
                response.getHeaders(),
                response.getBody());

            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to update registration endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to update registration endpoint failed, and no response body returned", e);
        }
    }

    private String getClientCredentialsToken(AspspDetails aspspDetails) {
        // the spec states a scope value isn't strictly needed, but some ASPSPs do actually require it
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("openid");
        AccessTokenResponse accessTokenResponse = oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);
        return accessTokenResponse.getAccessToken();
    }
}
