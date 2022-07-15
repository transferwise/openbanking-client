package com.transferwise.openbanking.client.api.vrp;

import com.transferwise.openbanking.client.api.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPDetails;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBErrorResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
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
public class RestVrpClient extends BasePaymentClient implements VrpClient {

    private static final String BASE_ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/%s/%s";

    private static final String VRP_CONSENT_RESOURCE = "domestic-vrp-consents";
    private static final String CONSENT_BY_ID_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{consentId}";
    private static final String FUNDS_CONFIRMATION_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{consentId}/funds-confirmation";

    private static final String VRP_RESOURCE = "domestic-vrps";
    private static final String VRP_BY_ID_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{domesticVrpId}";
    private static final String VRP_DETAILS_BY_ID_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{domesticVrpId}/payment-details";

    private final IdempotencyKeyGenerator<OBDomesticVRPConsentRequest, OBDomesticVRPRequest> idempotencyKeyGenerator;
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestVrpClient(
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

        log.debug("method=createDomesticVrpConsentRequest body={} headers={}", body, headers);
        log.info("Calling create VRP consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateVrpApiUrl(BASE_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.POST,
                request,
                String.class
            );
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new VrpApiCallException("Call to create VRP consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'", e, errorResponse);
        } catch (RestClientException e) {
            throw new VrpApiCallException("Call to create VRP consent endpoint failed, and no response body returned", e);
        }
        log.debug("method=createDomesticVrpConsentResponse code={} body={} headers={}", response.getStatusCode().value(), response.getBody(), response.getHeaders());
        OBDomesticVRPConsentResponse domesticVRPConsentResponse = jsonConverter.readValue(response.getBody(),
            OBDomesticVRPConsentResponse.class);
        validateResponse(domesticVRPConsentResponse);

        return domesticVRPConsentResponse;
    }

    @Override
    public OBVRPFundsConfirmationResponse getFundsConfirmation(
        String consentId,
        OBVRPFundsConfirmationRequest fundsConfirmationRequest,
        String accessToken,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {
        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(
            aspspDetails.getOrganisationId(),
            accessToken,
            null,
            jwtClaimsSigner.createDetachedSignature(fundsConfirmationRequest, aspspDetails, softwareStatementDetails)
        );

        String body = jsonConverter.writeValueAsString(fundsConfirmationRequest);
        HttpEntity<?> request = new HttpEntity<>(body, headers);

        log.debug("method=getFundsConfirmationRequest body={} headers={}", body, headers);
        log.info("Calling get VRP confirmation of funds API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateVrpApiUrl(FUNDS_CONFIRMATION_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.POST,
                request,
                String.class,
                consentId
            );
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new VrpApiCallException("Call to get VRP confirmation of funds endpoint failed, body returned '" + e.getResponseBodyAsString() + "'", e, errorResponse);
        } catch (RestClientException e) {
            throw new VrpApiCallException("Call to get VRP confirmation of funds endpoint failed, and no response body returned", e);
        }

        log.debug("method=getFundsConfirmationResponse code={} body={} headers={}", response.getStatusCode().value(), response.getBody(), response.getHeaders());
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

        log.debug("method=getDomesticVrpConsentRequest headers={}", headers);
        log.info("Calling get VRP consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateVrpApiUrl(CONSENT_BY_ID_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.GET,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new VrpApiCallException("Call to get VRP consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new VrpApiCallException("Call to get VRP consent endpoint failed, and no response body returned", e);
        }

        log.debug("method=getDomesticVrpConsentResponse code={} body={} headers={}", response.getStatusCode().value(), response.getBody(), response.getHeaders());
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

        log.debug("method=deleteDomesticVrpConsentRequest headers={}", headers);
        log.info("Calling delete VRP consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateVrpApiUrl(CONSENT_BY_ID_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.DELETE,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new VrpApiCallException("Call to delete VRP consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new VrpApiCallException("Call to delete VRP consent endpoint failed, and no response body returned", e);
        }

        log.debug("method=deleteDomesticVrpConsentResponse code={} body={} headers={}", response.getStatusCode().value(), response.getBody(), response.getHeaders());
        validateResponseCode(response);
    }

    @Override
    public OBDomesticVRPResponse submitDomesticVrp(
        OBDomesticVRPRequest vrpRequest,
        String accessToken,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {
        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(
            aspspDetails.getOrganisationId(),
            accessToken,
            idempotencyKeyGenerator.generateKeyForSubmission(vrpRequest),
            jwtClaimsSigner.createDetachedSignature(vrpRequest, aspspDetails, softwareStatementDetails));

        String body = jsonConverter.writeValueAsString(vrpRequest);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.debug("method=submitDomesticVrpRequest body={} headers={}", body, headers);
        log.info("Calling submit VRP API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateVrpApiUrl(BASE_ENDPOINT_PATH_FORMAT, VRP_RESOURCE, aspspDetails),
                HttpMethod.POST,
                request,
                String.class);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new VrpApiCallException("Call to submit VRP endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new VrpApiCallException("Call to submit VRP endpoint failed, and no response body returned", e);
        }

        log.debug("method=submitDomesticVrpResponse code={} body={} headers={}", response.getStatusCode().value(), response.getBody(), response.getHeaders());
        OBDomesticVRPResponse domesticVrpResponse = jsonConverter.readValue(response.getBody(),
            OBDomesticVRPResponse.class);
        validateResponse(domesticVrpResponse);

        return domesticVrpResponse;
    }

    @Override
    public OBDomesticVRPResponse getDomesticVrp(String domesticVrpId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.debug("method=getDomesticVrpRequest headers={}", headers);
        log.info("Calling get VRP API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateVrpApiUrl(VRP_BY_ID_ENDPOINT_PATH_FORMAT, VRP_RESOURCE, aspspDetails),
                HttpMethod.GET,
                request,
                String.class,
                domesticVrpId);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new VrpApiCallException("Call to get VRP endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new VrpApiCallException("Call to get VRP endpoint failed, and no response body returned", e);
        }

        log.debug("method=getDomesticVrpResponse code={} body={} headers={}", response.getStatusCode().value(), response.getBody(), response.getHeaders());
        OBDomesticVRPResponse domesticVrpResponse = jsonConverter.readValue(response.getBody(),
            OBDomesticVRPResponse.class);
        validateResponse(domesticVrpResponse);

        return domesticVrpResponse;
    }

    @Override
    public OBDomesticVRPDetails getDomesticVrpDetails(String domesticVrpId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.debug("method=getDomesticVrpDetailsRequest headers={}", headers);
        log.info("Calling get VRP API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateVrpApiUrl(VRP_DETAILS_BY_ID_ENDPOINT_PATH_FORMAT, VRP_RESOURCE, aspspDetails),
                HttpMethod.GET,
                request,
                String.class,
                domesticVrpId);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new VrpApiCallException("Call to get VRP endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new VrpApiCallException("Call to get VRP endpoint failed, and no response body returned", e);
        }

        log.debug("method=getDomesticVrpDetailsResponse code={} body={} headers={}", response.getStatusCode().value(), response.getBody(), response.getHeaders());
        OBDomesticVRPDetails domesticVrpDetailsResponse = jsonConverter.readValue(response.getBody(),
            OBDomesticVRPDetails.class);
        validateResponse(domesticVrpDetailsResponse);

        return domesticVrpDetailsResponse;
    }

    private void validateResponse(OBDomesticVRPConsentResponse response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getConsentId() == null ||
            response.getData().getConsentId().isBlank()
        ) {
            throw new VrpApiCallException("Empty or partial VRP consent response returned ");
        }
    }

    private void validateResponse(OBVRPFundsConfirmationResponse response) {
        if (response == null || response.getData() == null) {
            throw new VrpApiCallException("Empty or partial VRP funds confirmation response returned ");
        }
    }

    private void validateResponseCode(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            throw new VrpApiCallException("Call to delete VRP consent endpoint failed. Status code " + response.getStatusCode().name());
        }
        log.info("Call to delete VRP consent endpoint failed with unexpected status code {}", response.getStatusCode().value());
        throw new VrpApiCallException("Call to delete VRP consent endpoint failed. Status code " + response.getStatusCode().name());
    }


    private void validateResponse(OBDomesticVRPResponse response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getConsentId() == null ||
            response.getData().getConsentId().isBlank() ||
            response.getData().getDomesticVRPId() == null ||
            response.getData().getDomesticVRPId().isBlank()) {
            throw new VrpApiCallException("Empty or partial domestic VRP response returned ");
        }
    }

    private void validateResponse(OBDomesticVRPDetails response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getPaymentStatus() == null ||
            response.getData().getPaymentStatus().isEmpty()) {
            throw new VrpApiCallException("Empty or partial domestic VRP details response returned " + response);
        }
    }

    private OBErrorResponse1 mapBodyToObErrorResponse(String responseBodyAsString) {
        try {
            return jsonConverter.readValue(responseBodyAsString, OBErrorResponse1.class);
        } catch (Exception ex) {
            return null;
        }
    }
}
