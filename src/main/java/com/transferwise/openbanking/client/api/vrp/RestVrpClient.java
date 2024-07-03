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
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import wiremock.org.apache.commons.lang3.Validate;

@Slf4j
@SuppressWarnings({"checkstyle:abbreviationaswordinname", "checkstyle:parametername"})
public class RestVrpClient extends BasePaymentClient implements VrpClient {

    private static final String BASE_ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/%s/%s";

    private static final String VRP_CONSENT_RESOURCE = "domestic-vrp-consents";
    private static final String CONSENT_BY_ID_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{consentId}";
    private static final String FUNDS_CONFIRMATION_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{consentId}/funds-confirmation";

    private static final String VRP_RESOURCE = "domestic-vrps";
    private static final String VRP_BY_ID_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{domesticVrpId}";
    private static final String VRP_DETAILS_BY_ID_ENDPOINT_PATH_FORMAT = BASE_ENDPOINT_PATH_FORMAT + "/{domesticVrpId}/payment-details";

    private static final String ON_ERROR_GET_VRP_COF_LOG = "Call to get VRP confirmation of funds endpoint failed";
    private static final String ON_ERROR_GET_VRP_CONSENT_LOG = "Call to get VRP consent endpoint failed";
    private static final String ON_ERROR_DELETE_VRP_CONSENT_LOG = "Call to delete VRP consent endpoint";
    private static final String ON_ERROR_SUBMIT_VRP_LOG = "Call to submit VRP endpoint failed";
    private static final String ON_ERROR_GET_VRP_LOG = "Call to get VRP endpoint failed";

    private final IdempotencyKeyGenerator<OBDomesticVRPConsentRequest, OBDomesticVRPRequest> idempotencyKeyGenerator;
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestVrpClient(
        WebClient webClient,
        JsonConverter jsonConverter,
        OAuthClient oAuthClient,
        IdempotencyKeyGenerator<OBDomesticVRPConsentRequest, OBDomesticVRPRequest> idempotencyKeyGenerator,
        JwtClaimsSigner jwtClaimsSigner
    ) {
        super(webClient, jsonConverter, oAuthClient);
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

        return webClient.post()
            .uri(generateVrpApiUrl(BASE_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails))
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .exchangeToMono(
                clientResponse -> exchangeToMonoWithLog(clientResponse, "createDomesticVrpConsentResponse", OBDomesticVRPConsentResponse.class)
            )
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, "Call to create VRP consent endpoint failed"))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, "Call to create VRP consent endpoint failed"))
            .block();
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

        return webClient.post()
            .uri(generateVrpApiUrl(FUNDS_CONFIRMATION_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails), consentId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .exchangeToMono(
                clientResponse -> exchangeToMonoWithLog(clientResponse, "getFundsConfirmationResponse", OBVRPFundsConfirmationResponse.class)
            )
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class,
                e -> handleWebClientResponseException(e, ON_ERROR_GET_VRP_COF_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_GET_VRP_COF_LOG))
            .block();
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

        return webClient.get()
            .uri(generateVrpApiUrl(CONSENT_BY_ID_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails), consentId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .exchangeToMono(
                clientResponse -> exchangeToMonoWithLog(clientResponse, "getDomesticVrpConsentResponse", OBDomesticVRPConsentResponse.class)
            )
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_GET_VRP_CONSENT_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_GET_VRP_CONSENT_LOG))
            .block();
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

        webClient.delete()
            .uri(generateVrpApiUrl(CONSENT_BY_ID_ENDPOINT_PATH_FORMAT, VRP_CONSENT_RESOURCE, aspspDetails), consentId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .exchangeToMono(clientResponse -> {
                validateResponseCode(clientResponse.statusCode());
                return exchangeToMonoWithLog(clientResponse, "deleteDomesticVrpConsentResponse", String.class);
            })
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_DELETE_VRP_CONSENT_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_DELETE_VRP_CONSENT_LOG))
            .block();
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

        return webClient.post()
            .uri(generateVrpApiUrl(BASE_ENDPOINT_PATH_FORMAT, VRP_RESOURCE, aspspDetails))
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .exchangeToMono(clientResponse -> {
                validateResponseCode(clientResponse.statusCode());
                return exchangeToMonoWithLog(clientResponse, "submitDomesticVrpResponse", OBDomesticVRPResponse.class);
            })
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_SUBMIT_VRP_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_SUBMIT_VRP_LOG))
            .block();
    }

    @Override
    public OBDomesticVRPResponse getDomesticVrp(String domesticVrpId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.debug("method=getDomesticVrpRequest headers={}", headers);
        log.info("Calling get VRP API, with interaction ID {}", headers.getInteractionId());

        return webClient.get()
            .uri(generateVrpApiUrl(VRP_BY_ID_ENDPOINT_PATH_FORMAT, VRP_RESOURCE, aspspDetails), domesticVrpId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .exchangeToMono(clientResponse -> {
                validateResponseCode(clientResponse.statusCode());
                return exchangeToMonoWithLog(clientResponse, "getDomesticVrpResponse", OBDomesticVRPResponse.class);
            })
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_GET_VRP_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_GET_VRP_LOG))
            .block();
    }

    @Override
    public OBDomesticVRPDetails getDomesticVrpDetails(String domesticVrpId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.debug("method=getDomesticVrpDetailsRequest headers={}", headers);
        log.info("Calling get VRP API, with interaction ID {}", headers.getInteractionId());

        return webClient.get()
            .uri(generateVrpApiUrl(VRP_DETAILS_BY_ID_ENDPOINT_PATH_FORMAT, VRP_RESOURCE, aspspDetails), domesticVrpId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .exchangeToMono(clientResponse -> {
                validateResponseCode(clientResponse.statusCode());
                return exchangeToMonoWithLog(clientResponse, "getDomesticVrpDetailsResponse", OBDomesticVRPDetails.class);
            })
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_GET_VRP_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_GET_VRP_LOG))
            .block();
    }

    private void validateResponse(OBDomesticVRPConsentResponse response) {
        if (response == null
            || response.getData() == null
            || response.getData().getStatus() == null
            || response.getData().getConsentId() == null
            || response.getData().getConsentId().isBlank()
        ) {
            throw new VrpApiCallException("Empty or partial VRP consent response returned ");
        }
    }

    private void validateResponse(OBVRPFundsConfirmationResponse response) {
        if (response == null || response.getData() == null) {
            throw new VrpApiCallException("Empty or partial VRP funds confirmation response returned ");
        }
    }

    private void validateResponse(OBDomesticVRPResponse response) {
        if (response == null
            || response.getData() == null
            || response.getData().getStatus() == null
            || response.getData().getConsentId() == null
            || response.getData().getConsentId().isBlank()
            || response.getData().getDomesticVRPId() == null
            || response.getData().getDomesticVRPId().isBlank()) {
            throw new VrpApiCallException("Empty or partial domestic VRP response returned ");
        }
    }

    private void validateResponse(OBDomesticVRPDetails response) {
        if (response == null
            || response.getData() == null
            || response.getData().getPaymentStatus() == null
            || response.getData().getPaymentStatus().isEmpty()) {
            throw new VrpApiCallException("Empty or partial domestic VRP details response returned " + response);
        }
    }

    private void validateResponseCode(HttpStatusCode statusCode) {
        if (statusCode.is2xxSuccessful()) {
            return;
        }
        if (statusCode.is4xxClientError()
            || statusCode.is5xxServerError()) {
            throw new VrpApiCallException("Call to delete VRP consent endpoint failed. Status code " + statusCode.value());
        }
        log.info("Call to delete VRP consent endpoint failed with unexpected status code {}", statusCode.value());
        throw new VrpApiCallException("Call to delete VRP consent endpoint failed. Status code " + statusCode.value());
    }

    private OBErrorResponse1 mapBodyToObErrorResponse(String responseBodyAsString) {
        try {
            return jsonConverter.readValue(responseBodyAsString, OBErrorResponse1.class);
        } catch (Exception ex) {
            return null;
        }
    }

    private <T> Mono<T> exchangeToMonoWithLog(ClientResponse clientResponse, String method, Class<T> clazz) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.createException().flatMap(Mono::error);
        }
        return clientResponse
            .bodyToMono(clazz)
            .doFinally(body -> log.debug("method={} code={} body={} headers={}",
                method,
                clientResponse.statusCode().value(),
                body,
                clientResponse.headers().asHttpHeaders()));
    }

    private <T> Mono<T> handleWebClientResponseException(WebClientResponseException e, String prefixLog) {
        var errorMessage = "%s, response status code %s, body returned '%s'".formatted(prefixLog, e.getStatusCode(), e.getResponseBodyAsString());
        log.error(errorMessage, e);
        return Mono.error(new VrpApiCallException(errorMessage, e, mapBodyToObErrorResponse(e.getResponseBodyAsString())));
    }

    private <T> Mono<T> handleWebClientException(WebClientException e, String prefixLog) {
        var errorMessage = "%s, and no response body returned".formatted(prefixLog);
        log.error(errorMessage, e);
        return Mono.error(new VrpApiCallException(errorMessage, e));
    }
}
