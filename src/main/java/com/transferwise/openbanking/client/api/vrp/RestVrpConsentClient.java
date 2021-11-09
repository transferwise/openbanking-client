package com.transferwise.openbanking.client.api.vrp;

import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

@Slf4j
public class RestVrpConsentClient extends BasePaymentClient implements VrpConsentClient {

    private static final String VRP_CONSENT_RESOURCE = "domestic-vrp-consents";
    private static final String BASE_ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/vrp/%s";
    private static final String CONSENT_BY_ID_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{consentId}";
    private static final String FUNDS_CONFIRMATION_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{consentId}/funds-confirmation";

    private final IdempotencyKeyGenerator<OBDomesticVRPConsentRequest, OBDomesticVRPRequest> idempotencyKeyGenerator;
    private final JwtClaimsSigner jwtClaimsSigner;

    protected RestVrpConsentClient(
        RestOperations restOperations,
        JsonConverter jsonConverter,
        OAuthClient oAuthClient,
        IdempotencyKeyGenerator<OBDomesticVRPConsentRequest, OBDomesticVRPRequest> idempotencyKeyGenerator,
        JwtClaimsSigner jwtClaimsSigner
    ) {
        super(restOperations, jsonConverter, oAuthClient);
        this.idempotencyKeyGenerator = idempotencyKeyGenerator;
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    /**
     * TODO remove this comment
     * x-fapi-auth-date - is not required
     * x-fapi-customer-ip-address - is not required
     * x-customer-user-agent - is not required
     */
    @Override
    public OBDomesticVRPConsentResponse createDomesticVrpConsent(
        OBDomesticVRPConsentRequest domesticVRPConsentRequest,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {
        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails),
            idempotencyKeyGenerator.generateKeyForSetup(domesticVRPConsentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticVRPConsentRequest,
                aspspDetails,
                softwareStatementDetails
            ));

        String body = jsonConverter.writeValueAsString(domesticVRPConsentRequest);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.info("Calling create VRP consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(generateApiUrl(BASE_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.POST,
                request,
                String.class
            );
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to create VRP consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'", e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to create VRP consent endpoint failed, and no response body returned", e);
        }

        OBDomesticVRPConsentResponse domesticVRPConsentResponse = jsonConverter.readValue(response.getBody(),
            OBDomesticVRPConsentResponse.class);
        validateResponse(domesticVRPConsentResponse);

        return domesticVRPConsentResponse;
    }

    @Override
    public OBVRPFundsConfirmationResponse getFundsConfirmation(
        String consentId,
        OBVRPFundsConfirmationRequest fundsConfirmationRequest,
        AuthorizationContext authorizationContext,
        AspspDetails aspspDetails
    ) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            exchangeAuthorizationCode(authorizationContext, aspspDetails)
        );

        String body = jsonConverter.writeValueAsString(fundsConfirmationRequest);
        HttpEntity<?> request = new HttpEntity<>(body, headers);

        log.info("Calling get VRP confirmation of funds API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(FUNDS_CONFIRMATION_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.GET,
                request,
                String.class,
                consentId
            );
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get VRP confirmation of funds endpoint failed, body returned '" + e.getResponseBodyAsString() + "'", e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get VRP confirmation of funds endpoint failed, and no response body returned", e);
        }

        OBVRPFundsConfirmationResponse fundsConfirmationResponse = jsonConverter.readValue(response.getBody(),
            OBVRPFundsConfirmationResponse.class);
        validateResponse(fundsConfirmationResponse);

        return fundsConfirmationResponse;
    }

    @Override
    public OBDomesticVRPConsentResponse getDomesticVrpConsent(String consentId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails)
        );

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get VRP consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(CONSENT_BY_ID_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.GET,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get VRP consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get VRP consent endpoint failed, and no response body returned", e);
        }

        OBDomesticVRPConsentResponse domesticVRPConsentResponse = jsonConverter.readValue(response.getBody(),
            OBDomesticVRPConsentResponse.class);
        validateResponse(domesticVRPConsentResponse);

        return domesticVRPConsentResponse;
    }

    @Override
    public void deleteDomesticVrpConsent(String consentId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails)
        );

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling delete VRP consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(CONSENT_BY_ID_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.DELETE,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to delete VRP consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to delete VRP consent endpoint failed, and no response body returned", e);
        }

        validateResponseCode(response);
    }

    private void validateResponse(OBDomesticVRPConsentResponse response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getConsentId() == null ||
            response.getData().getConsentId().isBlank()) {
            throw new ApiCallException("Empty or partial VRP consent response returned " + response);
        }
    }

    private void validateResponse(OBVRPFundsConfirmationResponse response) {
        if (response == null || response.getData() == null) {
            throw new ApiCallException("Empty or partial VRP funds confirmation response returned " + response);
        }
    }

    private void validateResponseCode(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            throw new ApiCallException("Call to delete VRP consent endpoint failed. Status code " + response.getStatusCode().name());
        }
        log.info("Call to delete VRP consent endpoint failed with unexpected status code {}", response.getStatusCode().value());
        throw new ApiCallException("Call to delete VRP consent endpoint failed. Status code " + response.getStatusCode().name());
    }
}
