package com.transferwise.openbanking.client.api.payment.v3;

import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_CREATE_PAYMENT_LOG;
import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_GET_COF_LOG;
import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_GET_PAYMENT_CONSENT_LOG;
import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_GET_PAYMENT_LOG;
import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_SUBMIT_PAYMENT_LOG;
import static com.transferwise.openbanking.client.api.common.ExceptionUtils.handleWebClientException;

import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBErrorResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteFundsConfirmationResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import wiremock.org.apache.commons.lang3.Validate;

@Slf4j
@SuppressWarnings("checkstyle:parametername")
public class RestPaymentClient extends BasePaymentClient implements PaymentClient {

    private static final String ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/pisp/%s";
    private static final String PAYMENT_CONSENT_RESOURCE = "domestic-payment-consents";
    private static final String PAYMENT_RESOURCE = "domestic-payments";

    private final IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator;
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestPaymentClient(
        WebClient webClient,
        JsonConverter jsonConverter,
        OAuthClient oAuthClient,
        IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator,
        JwtClaimsSigner jwtClaimsSigner
    ) {
        super(webClient, jsonConverter, oAuthClient);
        this.idempotencyKeyGenerator = idempotencyKeyGenerator;
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    @Override
    public OBWriteDomesticConsentResponse5 createDomesticPaymentConsent(
        OBWriteDomesticConsent4 domesticPaymentConsentRequest,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails),
            idempotencyKeyGenerator.generateKeyForSetup(domesticPaymentConsentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        String body = jsonConverter.writeValueAsString(domesticPaymentConsentRequest);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.info("Calling create payment consent API, with interaction ID {}", headers.getInteractionId());

        return webClient.post()
            .uri(generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_CONSENT_RESOURCE, aspspDetails))
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .retrieve()
            .bodyToMono(OBWriteDomesticConsentResponse5.class)
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class,
                e -> handleWebClientResponseException(e, ON_ERROR_CREATE_PAYMENT_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_CREATE_PAYMENT_LOG, PaymentApiCallException.class))
            .block();
    }

    @Override
    public OBWriteDomesticResponse5 submitDomesticPayment(
        OBWriteDomestic2 domesticPaymentRequest,
        AuthorizationContext authorizationContext,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            exchangeAuthorizationCode(authorizationContext, aspspDetails).getAccessToken(),
            idempotencyKeyGenerator.generateKeyForSubmission(domesticPaymentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentRequest, aspspDetails, softwareStatementDetails));

        String body = jsonConverter.writeValueAsString(domesticPaymentRequest);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.info("Calling submit payment API, with interaction ID {}", headers.getInteractionId());

        return webClient.post()
            .uri(generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_RESOURCE, aspspDetails))
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .retrieve()
            .bodyToMono(OBWriteDomesticResponse5.class)
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_SUBMIT_PAYMENT_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_SUBMIT_PAYMENT_LOG, PaymentApiCallException.class))
            .block();
    }

    @Override
    public OBWriteDomesticConsentResponse5 getDomesticPaymentConsent(String consentId, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment consent API, with interaction ID {}", headers.getInteractionId());

        return webClient.get()
            .uri(generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_CONSENT_RESOURCE, aspspDetails) + "/{consentId}", consentId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .retrieve()
            .bodyToMono(OBWriteDomesticConsentResponse5.class)
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_GET_PAYMENT_CONSENT_LOG))
            .onErrorResume(WebClientException.class,
                e -> handleWebClientException(e, ON_ERROR_GET_PAYMENT_CONSENT_LOG, PaymentApiCallException.class))
            .block();
    }

    @Override
    public OBWriteDomesticResponse5 getDomesticPayment(String domesticPaymentId, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment API, with interaction ID {}", headers.getInteractionId());

        return webClient.get()
            .uri(generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_RESOURCE, aspspDetails) + "/{domesticPaymentId}", domesticPaymentId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .retrieve()
            .bodyToMono(OBWriteDomesticResponse5.class)
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_GET_PAYMENT_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_GET_PAYMENT_LOG, PaymentApiCallException.class))
            .block();
    }

    @Override
    public OBWriteFundsConfirmationResponse1 getFundsConfirmation(
        String consentId,
        AuthorizationContext authorizationContext,
        AspspDetails aspspDetails
    ) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            exchangeAuthorizationCode(authorizationContext, aspspDetails).getAccessToken());

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get confirmation of funds API, with interaction ID {}", headers.getInteractionId());

        return webClient.get()
            .uri(generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_CONSENT_RESOURCE, aspspDetails) + "/{consentId}/funds-confirmation", consentId)
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .retrieve()
            .bodyToMono(OBWriteFundsConfirmationResponse1.class)
            .doOnSuccess(this::validateResponse)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_GET_COF_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_GET_COF_LOG, PaymentApiCallException.class))
            .block();
    }

    private void validateResponse(OBWriteDomesticConsentResponse5 response) {
        if (response == null
            || response.getData() == null
            || response.getData().getStatus() == null
            || response.getData().getConsentId() == null
            || response.getData().getConsentId().isBlank()) {
            throw new PaymentApiCallException("Empty or partial domestic payment consent response returned " + response);
        }
    }

    private void validateResponse(OBWriteDomesticResponse5 response) {
        if (response == null
            || response.getData() == null
            || response.getData().getStatus() == null
            || response.getData().getDomesticPaymentId() == null
            || response.getData().getDomesticPaymentId().isBlank()) {
            throw new PaymentApiCallException("Empty or partial domestic payment response returned " + response);
        }
    }

    private void validateResponse(OBWriteFundsConfirmationResponse1 response) {
        if (response == null || response.getData() == null) {
            throw new PaymentApiCallException("Empty or partial funds confirmation response returned " + response);
        }
    }

    private OBErrorResponse1 mapBodyToObErrorResponse(String responseBodyAsString) {
        try {
            return jsonConverter.readValue(responseBodyAsString, OBErrorResponse1.class);
        } catch (Exception ex) {
            return null;
        }
    }

    private <T> Mono<T> handleWebClientResponseException(WebClientResponseException e, String prefixLog) {
        var errorMessage = "%s, response status code %s, body returned '%s'".formatted(prefixLog, e.getStatusCode(), e.getResponseBodyAsString());
        log.info(errorMessage, e);
        return Mono.error(new PaymentApiCallException(errorMessage, e, mapBodyToObErrorResponse(e.getResponseBodyAsString())));
    }
}
