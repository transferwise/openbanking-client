package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.oauth.domain.Scope;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("checkstyle:membername")
public class RestRegistrationClient implements RegistrationClient {

    private final JwtClaimsSigner jwtClaimsSigner;
    private final OAuthClient oAuthClient;
    private final WebClient webClient;

    @Override
    public ClientRegistrationResponse registerClient(
        ClientRegistrationRequest clientRegistrationRequest,
        AspspDetails aspspDetails
    ) {

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
            var prefixLog = "Received registration response";
            return webClient.post()
                .uri(aspspDetails.getRegistrationUrl())
                .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
                .bodyValue(Objects.requireNonNull(request.getBody()))
                .exchangeToMono(clientResponse -> exchangeToMonoWithLog(clientResponse, prefixLog, ClientRegistrationResponse.class))
                .block();
        } catch (WebClientResponseException e) {
            throw new ApiCallException("Call to register client endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (WebClientException e) {
            throw new ApiCallException("Call to register client endpoint failed, and no response body returned", e);
        }
    }

    @Override
    public ClientRegistrationResponse updateRegistration(
        ClientRegistrationRequest clientRegistrationRequest,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {

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
            var prefixLog = "Received update registration response";
            return webClient.put()
                .uri(aspspDetails.getRegistrationUrl() + "/{clientId}", aspspDetails.getClientId())
                .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
                .bodyValue(Objects.requireNonNull(request.getBody()))
                .exchangeToMono(clientResponse -> exchangeToMonoWithLog(clientResponse, prefixLog, ClientRegistrationResponse.class))
                .block();
        } catch (WebClientResponseException e) {
            throw new ApiCallException("Call to update registration endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (WebClientException e) {
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
            var prefixLog = "Received delete registration response";
            webClient.delete()
                .uri(aspspDetails.getRegistrationUrl() + "/{clientId}", aspspDetails.getClientId())
                .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
                .exchangeToMono(clientResponse -> exchangeToMonoWithLog(clientResponse, prefixLog, String.class))
                .block();
            return;
        } catch (WebClientResponseException e) {
            throw new ApiCallException("Call to delete registration endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (WebClientException e) {
            throw new ApiCallException("Call to delete registration endpoint failed, and no response body returned", e);
        }
    }

    private String getClientCredentialsToken(
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {
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

    private <T> Mono<T> exchangeToMonoWithLog(ClientResponse clientResponse, String prefixLog, Class<T> clazz) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.createException().flatMap(Mono::error);
        }
        var responseHeaders = clientResponse.headers().asHttpHeaders();
        return clientResponse.bodyToMono(clazz)
            .doFinally(body -> log.debug("{} '{}' and body '{}'",
                prefixLog,
                responseHeaders,
                body)
            );
    }
}
