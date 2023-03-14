package com.transferwise.openbanking.client.api.event;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

@Slf4j
@SuppressWarnings({"checkstyle:abbreviationaswordinname", "checkstyle:parametername"})
public class RestEventClient extends BasePaymentClient implements EventClient {

    private static final String BASE_ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/%s";
    private static final String EVENT_SUBSCRIPTION_RESOURCE = "event-subscriptions";
    private static final String BASE_ENDPOINT_WITH_EVENT_SUBSCRIPTION_ID_PATH_FORMAT =
        BASE_ENDPOINT_PATH_FORMAT + "/{eventSubscriptionId}";
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestEventClient(
        RestOperations restOperations,
        JsonConverter jsonConverter,
        OAuthClient oAuthClient,
        JwtClaimsSigner jwtClaimsSigner
    ) {
        super(restOperations, jsonConverter, oAuthClient);
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
        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateEventApiUrl(BASE_ENDPOINT_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails),
                HttpMethod.POST,
                request,
                String.class
            );
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new EventApiCallException(
                "Call to subscribe event endpoint failed, body returned '"
                    + e.getResponseBodyAsString() + "'", e, errorResponse);
        } catch (RestClientException e) {
            throw new EventApiCallException("Call to subscribe event endpoint failed, and no response body returned", e);
        }
        return jsonConverter.readValue(response.getBody(), OBEventSubscriptionResponse1.class);
    }

    @Override
    public OBEventSubscriptionsResponse1 getEventSubscriptions(AspspDetails aspspDetails)  {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails)
        );

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateEventApiUrl(BASE_ENDPOINT_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails),
                HttpMethod.GET,
                request,
                String.class
            );
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new EventApiCallException(
                "Call to get event resource endpoint failed, body returned '"
                    + e.getResponseBodyAsString() + "'", e, errorResponse);
        } catch (RestClientException e) {
            throw new EventApiCallException("Call to get event resource endpoint failed, and no response body returned", e);
        }

        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            throw new EventApiCallException("Call to get event resource endpoint failed. Status code " + response.getStatusCode().name());
        }
        OBEventSubscriptionsResponse1 eventSubscriptionResponse = jsonConverter.readValue(
            response.getBody(),
            OBEventSubscriptionsResponse1.class);
        return eventSubscriptionResponse;
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
        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateEventApiUrl(BASE_ENDPOINT_WITH_EVENT_SUBSCRIPTION_ID_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails),
                HttpMethod.PUT,
                request,
                String.class,
                changedResponse.getData().getEventSubscriptionId()
            );
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new EventApiCallException(
                "Call to change event resource endpoint failed, body returned '"
                    + e.getResponseBodyAsString() + "'", e, errorResponse);
        } catch (RestClientException e) {
            throw new EventApiCallException("Call to change event resource failed, and no response body returned", e);
        }
        return jsonConverter.readValue(response.getBody(), OBEventSubscriptionResponse1.class);
    }


    @Override
    public void deleteEventSubscription(String eventSubscriptionId, AspspDetails aspspDetails) {
        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails)
        );
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateEventApiUrl(BASE_ENDPOINT_WITH_EVENT_SUBSCRIPTION_ID_PATH_FORMAT, EVENT_SUBSCRIPTION_RESOURCE, aspspDetails),
                HttpMethod.DELETE,
                request,
                String.class,
                eventSubscriptionId
            );
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new EventApiCallException("Call to delete event endpoint failed, body returned '"
                + e.getResponseBodyAsString() + "'", e, errorResponse);
        } catch (RestClientException e) {
            throw new EventApiCallException("Call to delete event endpoint failed, and no response body returned", e);
        }

        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            throw new EventApiCallException(
                "Call to delete event subscription endpoint failed. Status code "
                    + response.getStatusCode().name());
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
