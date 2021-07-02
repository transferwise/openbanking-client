package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.api.common.BaseClient;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.api.registration.domain.ErrorResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
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

@Slf4j
public class RestRegistrationClient extends BaseClient implements RegistrationClient {

    private final JwtClaimsSigner jwtClaimsSigner;

    protected RestRegistrationClient(RestOperations restOperations,
                                     JsonConverter jsonConverter,
                                     JwtClaimsSigner jwtClaimsSigner) {
        super(restOperations, jsonConverter);
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    @Override
    public ApiResponse<ClientRegistrationResponse, ErrorResponse> registerClient(ClientRegistrationRequest clientRegistrationRequest,
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

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(aspspDetails.getRegistrationUrl(),
                HttpMethod.POST,
                request,
                String.class);

            log.debug("Received registration response with headers '{}' and body '{}'",
                response.getHeaders(),
                response.getBody());
        } catch (RestClientResponseException e) {
            return mapClientExceptionWithResponse(e, ErrorResponse.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        ClientRegistrationResponse clientRegistrationResponse = jsonConverter.readValue(response.getBody(),
            ClientRegistrationResponse.class);
        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), clientRegistrationResponse);
    }

    @Override
    public ApiResponse<ClientRegistrationResponse, ErrorResponse> updateRegistration(
        ClientRegistrationRequest clientRegistrationRequest,
        String clientCredentialsToken,
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
        headers.setBearerAuth(clientCredentialsToken);

        String signedClaims = jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails);
        HttpEntity<String> request = new HttpEntity<>(signedClaims, headers);

        log.debug("Sending update registration request to '{}' with headers '{}' and body '{}'",
            aspspDetails.getRegistrationUrl(),
            request.getHeaders(),
            request.getBody());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(aspspDetails.getRegistrationUrl() + "/{clientId}",
                HttpMethod.PUT,
                request,
                String.class,
                aspspDetails.getClientId());

            log.debug("Received update registration response with headers '{}' and body '{}'",
                response.getHeaders(),
                response.getBody());
        } catch (RestClientResponseException e) {
            return mapClientExceptionWithResponse(e, ErrorResponse.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        ClientRegistrationResponse clientRegistrationResponse = jsonConverter.readValue(response.getBody(),
            ClientRegistrationResponse.class);
        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), clientRegistrationResponse);
    }
}
