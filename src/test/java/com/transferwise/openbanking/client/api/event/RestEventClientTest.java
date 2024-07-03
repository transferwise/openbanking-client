package com.transferwise.openbanking.client.api.event;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "checkstyle:membername",
    "checkstyle:methodname",
    "checkstyle:abbreviationaswordinname"})
class RestEventClientTest {

    private static final String EVENT_SUBSCRIPTION_URL = "/open-banking/v3.1/event-subscriptions";
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
    private WireMockServer wireMockServer;
    private RestEventClient restEventClient;
    @Mock
    private OAuthClient oAuthClient;
    @Mock
    private JwtClaimsSigner jwtClaimsSigner;
    private List<String> events;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
        softwareStatementDetails = aSoftwareStatementDetails();
        accessTokenResponse = aAccessTokenResponse();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();

        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        WebClient webClient = WebClient.create("http://localhost:" + wireMockServer.port());
        aspspDetails = AspspDetailsFactory.aTestAspspDetails("http://localhost:" + wireMockServer.port());

        restEventClient = new RestEventClient(
            restTemplate,
            webClient,
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

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
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

        WireMock.stubFor(post(urlEqualTo(EVENT_SUBSCRIPTION_URL))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo(BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .withHeader(INTERACTION_ID, matching(".+"))
            .withHeader(FINANCIAL_ID, equalTo(aspspDetails.getOrganisationId()))
            .withHeader(JWS_SIGNATURE, equalTo(DETACHED_JWS_SIGNATURE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalTo(jsonConverter.writeValueAsString(eventSubscriptionRequest)))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        var response = restEventClient.createEventSubscription(eventSubscriptionRequest, aspspDetails, softwareStatementDetails);
        Assertions.assertEquals(mockEventSubscriptionResponse, response);
        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(EVENT_SUBSCRIPTION_URL)));
    }

    @Test
    void getAllEventSubscriptions() {
        OBEventSubscriptionsResponse1 mockEventSubscriptionsResponse = aOBEventSubscriptionsResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockEventSubscriptionsResponse);
        WireMock.stubFor(get(urlEqualTo(EVENT_SUBSCRIPTION_URL))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo(BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .withHeader(INTERACTION_ID, matching(".+"))
            .withHeader(FINANCIAL_ID, equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));
        var response = restEventClient.getEventSubscriptions(aspspDetails);
        Assertions.assertEquals(mockEventSubscriptionsResponse, response);
        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(EVENT_SUBSCRIPTION_URL)));
    }

    @Test
    void getAllEventSubscriptionsThrowException() {
        WireMock.stubFor(get(urlEqualTo(EVENT_SUBSCRIPTION_URL)).willReturn(serverError()));
        Assertions.assertThrows(EventApiCallException.class, () -> restEventClient.getEventSubscriptions(aspspDetails));
        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(EVENT_SUBSCRIPTION_URL)));
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
        WireMock.stubFor(put(urlEqualTo(EVENT_SUBSCRIPTION_URL + "/" + EVENT_SUBSCRIPTION_ID))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo(BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .withHeader(INTERACTION_ID, matching(".+"))
            .withHeader(FINANCIAL_ID, equalTo(aspspDetails.getOrganisationId()))
            .withHeader(JWS_SIGNATURE, equalTo(DETACHED_JWS_SIGNATURE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));
        var response = restEventClient.changeEventSubscription(eventSubscriptionOldResponse, aspspDetails, softwareStatementDetails);
        Assertions.assertEquals(mockEventSubscriptionResponse, response);
        WireMock.verify(exactly(1), putRequestedFor(urlEqualTo(EVENT_SUBSCRIPTION_URL + "/" + EVENT_SUBSCRIPTION_ID)));
    }

    @Test
    void deleteEventSubscription() {
        OBEventSubscriptionResponse1 mockEventSubscriptionResponse = aOBEventSubscriptionResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockEventSubscriptionResponse);
        WireMock.stubFor(delete(urlEqualTo(EVENT_SUBSCRIPTION_URL + "/" + EVENT_SUBSCRIPTION_ID))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo(BEARER_AUTHORISATION_PREFIX + " " + accessTokenResponse.getAccessToken()))
            .withHeader(INTERACTION_ID, matching(".+"))
            .withHeader(FINANCIAL_ID, equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));
        restEventClient.deleteEventSubscription(EVENT_SUBSCRIPTION_ID, aspspDetails);
        WireMock.verify(exactly(1), deleteRequestedFor(urlEqualTo(EVENT_SUBSCRIPTION_URL + "/" + EVENT_SUBSCRIPTION_ID)));
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
