package com.transferwise.openbanking.client.api.event;

import com.transferwise.openbanking.client.api.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBErrorResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

public class RestEventClient extends BasePaymentClient implements EventClient {

    private static final String BASE_ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/%s";
    private static final String EVENT_SUBSCRIPTION_RESOURCE = "event-subscriptions";

    private final JwtClaimsSigner jwtClaimsSigner;

    protected RestEventClient(RestOperations restOperations,
        JsonConverter jsonConverter,
        OAuthClient oAuthClient,
        JwtClaimsSigner jwtClaimsSigner
    ) {
        super(restOperations, jsonConverter, oAuthClient);
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    @Override
    public OBEventSubscriptionResponse1 subscribeToAnEvent(
        OBEventSubscription1 eventSubscriptionRequest, AspspDetails aspspDetails,
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
            throw new EventApiCallException("Call to subscribe event endpoint failed, body returned '" + e.getResponseBodyAsString() + "'", e, errorResponse);
        } catch (RestClientException e) {
            throw new EventApiCallException("Call to subscribe event endpoint failed, and no response body returned", e);
        }
        OBEventSubscriptionResponse1 eventSubscriptionResponse = jsonConverter.readValue(response.getBody(), OBEventSubscriptionResponse1.class);
        return eventSubscriptionResponse;
    }

    private OBErrorResponse1 mapBodyToObErrorResponse(String responseBodyAsString) {
        try {
            return jsonConverter.readValue(responseBodyAsString, OBErrorResponse1.class);
        } catch (Exception ex) {
            return null;
        }
    }

}
