package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.api.registration.domain.RegistrationPermission;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

    private String getClientCredentialsToken(AspspDetails aspspDetails,
                                             SoftwareStatementDetails softwareStatementDetails) {
        String scope = generateScopeValue(aspspDetails, softwareStatementDetails);
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest(scope);
        AccessTokenResponse accessTokenResponse = oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);
        return accessTokenResponse.getAccessToken();
    }

    private String generateScopeValue(AspspDetails aspspDetails, SoftwareStatementDetails softwareStatementDetails) {
        // The spec states a scope value isn't strictly needed, but some ASPSPs do actually require it, additionally
        // some require the scope to contain `openid` but some require it to not contain `openid`. As we request a
        // scope of what the permissions the software statement details currently has, we don't really support updating
        // the permissions of a client registration, but as this can't be modified in the Open Banking directory this
        // shouldn't be an issue.
        Set<RegistrationPermission> permissions = new LinkedHashSet<>(softwareStatementDetails.getPermissions());
        if (aspspDetails.registrationAuthenticationRequiresOpenIdScope()) {
            permissions.add(RegistrationPermission.OPENID);
        } else {
            permissions.remove(RegistrationPermission.OPENID);
        }

        return permissions.stream()
            .map(RegistrationPermission::getValue)
            .collect(Collectors.joining(" "));
    }
}
