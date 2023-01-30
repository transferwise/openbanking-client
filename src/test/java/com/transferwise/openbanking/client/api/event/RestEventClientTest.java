package com.transferwise.openbanking.client.api.event;

import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;

import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1Data;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1Data;
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
public class RestEventClientTest {

    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";
    public static final String EVENT_SUBSCRIPTION_URL = "https://aspsp.co.uk/open-banking/v3.1/event-subscriptions";

    @Mock
    private OAuthClient oAuthClient;
    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private MockRestServiceServer mockAspspServer;
    private static JsonConverter jsonConverter;
    private RestEventClient restEventClient;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
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
    }

    @Test
    void testSubscribeToAnEvent() {
        OBEventSubscription1 eventSubscriptionRequest = aEventSubscriptionRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();

        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);
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
                "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id",
                CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id",
                aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT,
                MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content()
                .json(jsonConverter.writeValueAsString(eventSubscriptionRequest)))
            .andRespond(
                MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBEventSubscriptionResponse1 eventSubscriptionResponse = restEventClient.subscribeToAnEvent(
            eventSubscriptionRequest, aspspDetails, softwareStatementDetails);
        Assertions.assertEquals(mockEventSubscriptionResponse, eventSubscriptionResponse);
        mockAspspServer.verify();
    }

    private OBEventSubscription1 aEventSubscriptionRequest() {
        OBEventSubscription1Data data = new OBEventSubscription1Data().callbackUrl("callback-url")
            .eventTypes(
                List.of("event1"));
        return new OBEventSubscription1().data(data);
    }

    private OBEventSubscriptionResponse1 aOBEventSubscriptionResponse() {
        OBEventSubscriptionResponse1Data data = new OBEventSubscriptionResponse1Data().eventSubscriptionId(
            "event-subs-id").callbackUrl("callback-url").eventTypes(List.of("event1"));
        return new OBEventSubscriptionResponse1().data(data);
    }

}
