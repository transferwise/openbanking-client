package com.transferwise.openbanking.client.api.event;

import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_CHANGE_EVENT_RESOURCE_LOG;
import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_DELETE_EVENT_LOG;
import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_GET_EVENT_RESOURCE_LOG;
import static com.transferwise.openbanking.client.api.common.ErrorLogConstant.ON_ERROR_SUB_EVENT_LOG;
import static com.transferwise.openbanking.client.api.common.ExceptionUtils.handleWebClientException;

import com.transferwise.openbanking.client.api.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBErrorResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionsResponse1;
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
@SuppressWarnings({"checkstyle:abbreviationaswordinname", "checkstyle:parametername"})
public class RestEventClient extends BasePaymentClient implements EventClient {

    private static final String BASE_ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/%s";
    private static final String EVENT_SUBSCRIPTION_RESOURCE = "event-subscriptions";
    private static final String BASE_ENDPOINT_WITH_EVENT_SUBSCRIPTION_ID_PATH_FORMAT =
        BASE_ENDPOINT_PATH_FORMAT + "/{eventSubscriptionId}";
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestEventClient(
        WebClient webClient,
        JsonConverter jsonConverter,
        OAuthClient oAuthClient,
        JwtClaimsSigner jwtClaimsSigner
    ) {
        super(webClient, jsonConverter, oAuthClient);
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    @Override
    public OBEventSubscriptionResponse1 createEventSubscription(
        OBEventSubscription1 eventSubscriptionRequest,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails),
            null,
            jwtClaimsSigner.createDetachedSignature(eventSubscriptionRequest,
                aspspDetails,
                softwareStatementDetails
            ));

        String body = jsonConverter.writeValueAsString(eventSubscriptionRequest);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return webClient.post()
            .uri(generateEventApiUrl(BASE_ENDPOINT_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails))
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .retrieve()
            .bodyToMono(OBEventSubscriptionResponse1.class)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_SUB_EVENT_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_SUB_EVENT_LOG, EventApiCallException.class))
            .block();
    }

    @Override
    public OBEventSubscriptionsResponse1 getEventSubscriptions(AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails)
        );

        HttpEntity<?> request = new HttpEntity<>(headers);
        return webClient.get()
            .uri(generateEventApiUrl(BASE_ENDPOINT_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails))
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .retrieve()
            .bodyToMono(OBEventSubscriptionsResponse1.class)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_GET_EVENT_RESOURCE_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_GET_EVENT_RESOURCE_LOG, EventApiCallException.class))
            .block();
    }

    @Override
    public OBEventSubscriptionResponse1 changeEventSubscription(
        OBEventSubscriptionResponse1 changedResponse,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails),
            null,
            jwtClaimsSigner.createDetachedSignature(changedResponse,
                aspspDetails,
                softwareStatementDetails
            ));
        String body = jsonConverter.writeValueAsString(changedResponse);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return webClient.put()
            .uri(
                generateEventApiUrl(BASE_ENDPOINT_WITH_EVENT_SUBSCRIPTION_ID_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails),
                changedResponse.getData().getEventSubscriptionId()
            )
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .bodyValue(Validate.notNull(request.getBody()))
            .retrieve()
            .bodyToMono(OBEventSubscriptionResponse1.class)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_CHANGE_EVENT_RESOURCE_LOG))
            .onErrorResume(WebClientException.class,
                e -> handleWebClientException(e, ON_ERROR_CHANGE_EVENT_RESOURCE_LOG, EventApiCallException.class))
            .block();
    }


    @Override
    public void deleteEventSubscription(String eventSubscriptionId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails)
        );
        HttpEntity<?> request = new HttpEntity<>(headers);
        webClient.delete()
            .uri(
                generateEventApiUrl(BASE_ENDPOINT_WITH_EVENT_SUBSCRIPTION_ID_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails),
                eventSubscriptionId
            )
            .headers(httpHeaders -> httpHeaders.addAll(request.getHeaders()))
            .retrieve()
            .bodyToMono(String.class)
            .onErrorResume(WebClientResponseException.class, e -> handleWebClientResponseException(e, ON_ERROR_DELETE_EVENT_LOG))
            .onErrorResume(WebClientException.class, e -> handleWebClientException(e, ON_ERROR_DELETE_EVENT_LOG, EventApiCallException.class))
            .block();
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
        log.error(errorMessage, e);
        return Mono.error(new EventApiCallException(errorMessage, e, mapBodyToObErrorResponse(e.getResponseBodyAsString())));
    }
}
