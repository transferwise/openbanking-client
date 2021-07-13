package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
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
import java.util.stream.Collectors;

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
                                                         AspspDetails aspspDetails,
                                                         SoftwareStatementDetails softwareStatementDetails) {

        HttpHeaders headers = new HttpHeaders();
        if (aspspDetails.registrationUsesJoseContentType()) {
            headers.setContentType(MediaType.valueOf("application/jose"));
        } else {
            headers.setContentType(MediaType.valueOf("application/jwt"));
        }
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        // as we're using a raw String as the body type, we need to manually set the header
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(getClientCredentialsToken(aspspDetails, softwareStatementDetails));

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

    @Override
    public void deleteRegistration(AspspDetails aspspDetails, SoftwareStatementDetails softwareStatementDetails) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getClientCredentialsToken(aspspDetails, softwareStatementDetails));

        HttpEntity<String> request = new HttpEntity<>(headers);

        log.debug("Sending delete registration request to '{}/{}' with headers '{}'",
            aspspDetails.getRegistrationUrl(),
            aspspDetails.getClientId(),
            request.getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                aspspDetails.getRegistrationUrl() + "/{clientId}",
                HttpMethod.DELETE,
                request,
                String.class,
                aspspDetails.getClientId());

            log.debug("Received delete registration response with headers '{}' and body '{}'",
                response.getHeaders(),
                response.getBody());
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to delete registration endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to delete registration endpoint failed, and no response body returned", e);
        }
    }

    private String getClientCredentialsToken(AspspDetails aspspDetails,
                                             SoftwareStatementDetails softwareStatementDetails) {
        String scope = generateScopeValue(aspspDetails, softwareStatementDetails);
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest(scope);
        AccessTokenResponse accessTokenResponse = oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);
        return accessTokenResponse.getAccessToken();
    }

    private String generateScopeValue(AspspDetails aspspDetails, SoftwareStatementDetails softwareStatementDetails) {
        return aspspDetails.getRegistrationAuthenticationScopes(softwareStatementDetails)
            .stream()
            .map(Scope::getValue)
            .collect(Collectors.joining(" "));
    }
}
