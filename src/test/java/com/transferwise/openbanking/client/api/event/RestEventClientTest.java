package com.transferwise.openbanking.client.api.event;

import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;

import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1Data;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1Data;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionsResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionsResponse1Data;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionsResponse1DataEventSubscription;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JacksonJsonConverter;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.test.factory.AspspDetailsFactory;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "checkstyle:membername",
    "checkstyle:methodname",
    "checkstyle:abbreviationaswordinname"})
public class RestEventClientTest {

    private static final String EVENT_SUBSCRIPTION_URL = "https://aspsp.co.uk/open-banking/v3.1/event-subscriptions";
    private static final String CALLBACK_URL = "callback-url";
    private static final String EVENT_SUBSCRIPTION_ID = "event-subs-id";
    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";
    private static final String INTERACTION_ID = "x-fapi-interaction-id";
    private static final String FINANCIAL_ID = "x-fapi-financial-id";
    private static final String BEARER_AUTHORISATION_PREFIX = "Bearer";
    private static final String JWS_SIGNATURE = "x-jws-signature";
    private static JsonConverter jsonConverter;
    private static AccessTokenResponse accessTokenResponse;
    private static AspspDetails aspspDetails;
    private static SoftwareStatementDetails softwareStatementDetails;
    private MockRestServiceServer mockAspspServer;
    private RestEventClient restEventClient;
    @Mock
    private OAuthClient oAuthClient;
    @Mock
    private JwtClaimsSigner jwtClaimsSigner;
    private List<String> events;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
        aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        softwareStatementDetails = aSoftwareStatementDetails();
        accessTokenResponse = aAccessTokenResponse();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);
        restEventClient = new RestEventClient(
            restTemplate,
            jsonConverter,
            oAuthClient,
            jwtClaimsSigner);
        events = List.of("event1");
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);
    }

    @Test
    void testSubscribeToAnEvent() {
        OBEventSubscription1 eventSubscriptionRequest = aEventSubscriptionRequest();
        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    eventSubscriptionRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBEventSubscriptionResponse1 mockEventSubscriptionResponse = aOBEventSubscriptionResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockEventSubscriptionResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(EVENT_SUBSCRIPTION_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION,
                BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header(INTERACTION_ID,
                CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header(FINANCIAL_ID,
                aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(JWS_SIGNATURE, DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT,
                MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content()
                .json(jsonConverter.writeValueAsString(eventSubscriptionRequest)))
            .andRespond(
                MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        var response = restEventClient.createEventSubscription(eventSubscriptionRequest, aspspDetails, softwareStatementDetails);
        Assertions.assertEquals(mockEventSubscriptionResponse, response);
        mockAspspServer.verify();
    }

    @Test
    void getAllEventSubscriptions() {
        OBEventSubscriptionsResponse1 mockEventSubscriptionsResponse = aOBEventSubscriptionsResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockEventSubscriptionsResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(EVENT_SUBSCRIPTION_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET)).andExpect(
                MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION,
                    BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header(INTERACTION_ID, CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header(FINANCIAL_ID, aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        var response = restEventClient.getEventSubscriptions(aspspDetails);
        Assertions.assertEquals(mockEventSubscriptionsResponse, response);
        mockAspspServer.verify();
    }

    @Test
    void changeEventSubscriptions() {
        OBEventSubscriptionResponse1Data data = new OBEventSubscriptionResponse1Data()
            .eventSubscriptionId(EVENT_SUBSCRIPTION_ID)
            .callbackUrl(CALLBACK_URL)
            .eventTypes(events);
        OBEventSubscriptionResponse1 eventSubscriptionOldResponse = new OBEventSubscriptionResponse1().data(data);
        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    eventSubscriptionOldResponse,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBEventSubscriptionResponse1 mockEventSubscriptionResponse = aOBEventSubscriptionResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockEventSubscriptionResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(EVENT_SUBSCRIPTION_URL + "/" + EVENT_SUBSCRIPTION_ID))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT)).andExpect(
                MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION,
                    BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header(INTERACTION_ID, CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header(FINANCIAL_ID, aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(JWS_SIGNATURE, DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        var response = restEventClient.changeEventSubscription(eventSubscriptionOldResponse, aspspDetails, softwareStatementDetails);
        Assertions.assertEquals(mockEventSubscriptionResponse, response);
        mockAspspServer.verify();
    }

    @Test
    void deleteEventSubscription() {
        OBEventSubscriptionResponse1 mockEventSubscriptionResponse = aOBEventSubscriptionResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockEventSubscriptionResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(EVENT_SUBSCRIPTION_URL + "/" + EVENT_SUBSCRIPTION_ID))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.DELETE)).andExpect(
                MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION,
                    BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header(INTERACTION_ID, CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header(FINANCIAL_ID, aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        restEventClient.deleteEventSubscription(EVENT_SUBSCRIPTION_ID, aspspDetails);
        mockAspspServer.verify();
    }

    private OBEventSubscription1 aEventSubscriptionRequest() {
        OBEventSubscription1Data data = new OBEventSubscription1Data().callbackUrl(CALLBACK_URL).eventTypes(events);
        return new OBEventSubscription1().data(data);
    }

    private OBEventSubscriptionResponse1 aOBEventSubscriptionResponse() {
        OBEventSubscriptionResponse1Data data =
            new OBEventSubscriptionResponse1Data().eventSubscriptionId(EVENT_SUBSCRIPTION_ID).callbackUrl(CALLBACK_URL).eventTypes(events);
        return new OBEventSubscriptionResponse1().data(data);
    }

    private OBEventSubscriptionsResponse1 aOBEventSubscriptionsResponse() {
        OBEventSubscriptionsResponse1DataEventSubscription eventSubscription =
            new OBEventSubscriptionsResponse1DataEventSubscription().eventSubscriptionId(EVENT_SUBSCRIPTION_ID).callbackUrl(CALLBACK_URL)
                .eventTypes(events);
        return new OBEventSubscriptionsResponse1().data(new OBEventSubscriptionsResponse1Data().eventSubscription(List.of(eventSubscription)));
    }
}
