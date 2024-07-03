package com.transferwise.openbanking.client.api.payment.v3;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.AuthorizationContextFactory.aAuthorizationContext;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBExternalAccountIdentification4Code;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBRisk1;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2Data;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2DataInitiation;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2DataInitiationCreditorAccount;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2DataInitiationInstructedAmount;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2DataInitiationRemittanceInformation;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsent4Data;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsent4DataAuthorisation;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsentResponse5Data;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5Data;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteFundsConfirmationResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteFundsConfirmationResponse1Data;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteFundsConfirmationResponse1DataFundsAvailableResult;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JacksonJsonConverter;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.test.factory.AspspDetailsFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "checkstyle:membername", "checkstyle:variabledeclarationusagedistance", "checkstyle:methodname"})
class AsyncPaymentClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";

    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";

    private static JsonConverter jsonConverter;

    private static AspspDetails aspspDetails;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private WireMockServer wireMockServer;

    private AsyncPaymentClient asyncPaymentClient;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
    }

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        WebClient webClient = WebClient.create("http://localhost:" + wireMockServer.port());
        aspspDetails = AspspDetailsFactory.aTestAspspDetails("http://localhost:" + wireMockServer.port());

        asyncPaymentClient = new AsyncPaymentClient(
            webClient,
            jsonConverter,
            oAuthClient,
            idempotencyKeyGenerator,
            jwtClaimsSigner);
    }

    @Test
    void createDomesticPaymentConsent() {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(domesticPaymentConsentRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    domesticPaymentConsentRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBWriteDomesticConsentResponse5 mockPaymentConsentResponse = aDomesticPaymentConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockPaymentConsentResponse);
        WireMock.stubFor(post(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents"))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader("x-jws-signature", equalTo(DETACHED_JWS_SIGNATURE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalTo(jsonConverter.writeValueAsString(domesticPaymentConsentRequest)))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBWriteDomesticConsentResponse5 paymentConsentResponse = asyncPaymentClient.createDomesticPaymentConsent(
            domesticPaymentConsentRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockPaymentConsentResponse, paymentConsentResponse);

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents")));
    }

    @Test
    void createDomesticPaymentConsentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        WireMock.stubFor(post(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents")).willReturn(serverError()));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents")));
    }

    @Test
    void createDomesticPaymentConsentThrowsPaymentApiCallExceptionOnResponseWithNoContent() {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        WireMock.stubFor(post(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents")).willReturn(noContent()));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents")));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void createDomesticPaymentConsentThrowsPaymentApiCallExceptionOnPartialResponse(OBWriteDomesticConsentResponse5 response)
        throws Exception {

        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(post(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents"))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents")));
    }

    @Test
    void submitDomesticPayment() throws Exception {
        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "authorization_code".equals(request.getRequestBody().get("grant_type"))
                        && authorizationContext.getAuthorizationCode().equals(request.getRequestBody().get("code"))
                        && authorizationContext.getRedirectUrl().equals(request.getRequestBody().get("redirect_uri"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(domesticPaymentRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    domesticPaymentRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBWriteDomesticResponse5 mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentResponse);
        WireMock.stubFor(post(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments"))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader("x-idempotency-key", equalTo(IDEMPOTENCY_KEY))
            .withHeader("x-jws-signature", equalTo(DETACHED_JWS_SIGNATURE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalTo(jsonConverter.writeValueAsString(domesticPaymentRequest)))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBWriteDomesticResponse5 domesticPaymentResponse = asyncPaymentClient.submitDomesticPayment(domesticPaymentRequest,
            authorizationContext,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments")));
    }

    @Test
    void submitDomesticPaymentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBWriteDomestic2.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        WireMock.stubFor(post(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments")).willReturn(badRequest()));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.submitDomesticPayment(
                domesticPaymentRequest,
                authorizationContext,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments")));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void submitDomesticPaymentThrowsPaymentApiCallExceptionOnPartialResponse(OBWriteDomesticResponse5 response)
        throws Exception {

        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBWriteDomestic2.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents"))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.submitDomesticPayment(
                domesticPaymentRequest,
                authorizationContext,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments")));
    }

    @Test
    void getDomesticPaymentConsent() {
        String consentId = "consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBWriteDomesticConsentResponse5 mockDomesticPaymentConsentResponse = aDomesticPaymentConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentConsentResponse);
        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = asyncPaymentClient.getDomesticPaymentConsent(
            consentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentConsentResponse, domesticPaymentConsentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId)));
    }

    @Test
    void getDomesticPaymentConsentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        String consentId = "consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId)).willReturn(serverError()));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.getDomesticPaymentConsent(consentId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId)));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void getDomesticPaymentConsentThrowsPaymentApiCallExceptionPartialResponse(OBWriteDomesticConsentResponse5 response) {
        String consentId = "consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.getDomesticPaymentConsent(consentId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId)));
    }

    @Test
    void getDomesticPayment() throws Exception {
        String domesticPaymentId = "domestic-payment-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBWriteDomesticResponse5 mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentResponse);
        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBWriteDomesticResponse5 domesticPaymentResponse = asyncPaymentClient.getDomesticPayment(domesticPaymentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId)));
    }

    @Test
    void getDomesticPaymentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        String domesticPaymentId = "domestic-payment-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId)).willReturn(serverError()));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.getDomesticPayment(domesticPaymentId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId)));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void getDomesticPaymentThrowsPaymentApiCallExceptionPartialResponse(OBWriteDomesticResponse5 response) throws Exception {
        String domesticPaymentId = "domestic-payment-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.getDomesticPayment(domesticPaymentId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId)));
    }

    @Test
    void getFundsConfirmation() throws Exception {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "authorization_code".equals(request.getRequestBody().get("grant_type"))
                        && authorizationContext.getAuthorizationCode().equals(request.getRequestBody().get("code"))
                        && authorizationContext.getRedirectUrl().equals(request.getRequestBody().get("redirect_uri"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBWriteFundsConfirmationResponse1 mockFundsConfirmationResponse = aFundsConfirmationResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockFundsConfirmationResponse);
        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBWriteFundsConfirmationResponse1 fundsConfirmationResponse = asyncPaymentClient.getFundsConfirmation(consentId,
            authorizationContext,
            aspspDetails);

        Assertions.assertEquals(mockFundsConfirmationResponse, fundsConfirmationResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        WireMock.verify(exactly(1),
            getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation")));
    }

    @Test
    void getFundsConfirmationThrowsPaymentApiCallExceptionOnApiCallFailure() {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .willReturn(serverError()));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.getFundsConfirmation(consentId, authorizationContext, aspspDetails));

        WireMock.verify(exactly(1),
            getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation")));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialFundsConfirmationResponses.class)
    void getFundsConfirmationThrowsPaymentApiCallExceptionPartialResponse(OBWriteFundsConfirmationResponse1 response)
        throws Exception {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(get(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .willReturn(serverError()));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> asyncPaymentClient.getFundsConfirmation(consentId, authorizationContext, aspspDetails));

        WireMock.verify(exactly(1),
            getRequestedFor(urlEqualTo("/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation")));
    }

    private OBWriteDomesticConsent4 aDomesticPaymentConsentRequest() {
        OBWriteDomesticConsent4DataAuthorisation authorisation =
            new OBWriteDomesticConsent4DataAuthorisation()
                .authorisationType(OBWriteDomesticConsent4DataAuthorisation.AuthorisationTypeEnum.SINGLE);
        OBWriteDomestic2DataInitiationInstructedAmount instructedAmount =
            new OBWriteDomestic2DataInitiationInstructedAmount()
                .amount("1000.00")
                .currency("GBP");

        OBWriteDomestic2DataInitiationCreditorAccount creditorAccount =
            new OBWriteDomestic2DataInitiationCreditorAccount()
                .schemeName(OBExternalAccountIdentification4Code.SORTCODEACCOUNTNUMBER)
                .identification("112233123456")
                .name("TransferWise Ltd");
        OBWriteDomestic2DataInitiationRemittanceInformation remittanceInformation =
            new OBWriteDomestic2DataInitiationRemittanceInformation()
                .reference("reference");
        OBWriteDomestic2DataInitiation initiation = new OBWriteDomestic2DataInitiation()
            .instructionIdentification("instruction-identification")
            .endToEndIdentification("end-to-end-identification")
            .instructedAmount(instructedAmount)
            .creditorAccount(creditorAccount)
            .remittanceInformation(remittanceInformation);
        OBWriteDomesticConsent4Data data = new OBWriteDomesticConsent4Data()
            .authorisation(authorisation)
            .initiation(initiation);
        OBRisk1 risk = new OBRisk1()
            .paymentContextCode(OBRisk1.PaymentContextCodeEnum.OTHER);
        return new OBWriteDomesticConsent4()
            .data(data)
            .risk(risk);
    }

    private OBWriteDomestic2 aDomesticPaymentRequest() {
        OBWriteDomestic2DataInitiationInstructedAmount instructedAmount =
            new OBWriteDomestic2DataInitiationInstructedAmount()
                .amount("1000.00")
                .currency("GBP");
        OBWriteDomestic2DataInitiationCreditorAccount creditorAccount =
            new OBWriteDomestic2DataInitiationCreditorAccount()
                .schemeName(OBExternalAccountIdentification4Code.SORTCODEACCOUNTNUMBER)
                .identification("112233123456")
                .name("TransferWise Ltd");
        OBWriteDomestic2DataInitiationRemittanceInformation remittanceInformation =
            new OBWriteDomestic2DataInitiationRemittanceInformation()
                .reference("reference");
        OBWriteDomestic2DataInitiation initiation = new OBWriteDomestic2DataInitiation()
            .instructionIdentification("instruction-identification")
            .endToEndIdentification("end-to-end-identification")
            .instructedAmount(instructedAmount)
            .creditorAccount(creditorAccount)
            .remittanceInformation(remittanceInformation);
        OBWriteDomestic2Data data = new OBWriteDomestic2Data()
            .consentId("consent-id")
            .initiation(initiation);
        OBRisk1 risk = new OBRisk1()
            .paymentContextCode(OBRisk1.PaymentContextCodeEnum.OTHER);
        return new OBWriteDomestic2()
            .data(data)
            .risk(risk);
    }

    private OBWriteDomesticConsentResponse5 aDomesticPaymentConsentResponse() {
        OBWriteDomesticConsentResponse5Data data = new OBWriteDomesticConsentResponse5Data()
            .consentId("consent-id")
            .status(OBWriteDomesticConsentResponse5Data.StatusEnum.AUTHORISED);
        return new OBWriteDomesticConsentResponse5()
            .data(data);
    }

    private OBWriteDomesticResponse5 aDomesticPaymentResponse() {
        OBWriteDomesticResponse5Data data = new OBWriteDomesticResponse5Data()
            .domesticPaymentId("domestic-payment-id")
            .status(OBWriteDomesticResponse5Data.StatusEnum.ACCEPTEDSETTLEMENTINPROCESS);
        return new OBWriteDomesticResponse5()
            .data(data);
    }

    private OBWriteFundsConfirmationResponse1 aFundsConfirmationResponse() {
        OBWriteFundsConfirmationResponse1DataFundsAvailableResult fundsAvailableResult =
            new OBWriteFundsConfirmationResponse1DataFundsAvailableResult()
                .fundsAvailable(true);
        OBWriteFundsConfirmationResponse1Data data = new OBWriteFundsConfirmationResponse1Data()
            .fundsAvailableResult(fundsAvailableResult);
        return new OBWriteFundsConfirmationResponse1()
            .data(data);
    }

    private static class PartialDomesticPaymentConsentResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(null, OBWriteDomesticConsentResponse5Data.StatusEnum.CONSUMED)),
                Arguments.of(ofData("", OBWriteDomesticConsentResponse5Data.StatusEnum.CONSUMED)),
                Arguments.of(ofData(" ", OBWriteDomesticConsentResponse5Data.StatusEnum.CONSUMED)),
                Arguments.of(ofData("123", null))
            );
        }

        private OBWriteDomesticConsentResponse5 nullData() {
            return new OBWriteDomesticConsentResponse5();
        }

        private OBWriteDomesticConsentResponse5 ofData(String consentId,
            OBWriteDomesticConsentResponse5Data.StatusEnum status) {
            OBWriteDomesticConsentResponse5Data data = new OBWriteDomesticConsentResponse5Data()
                .consentId(consentId)
                .status(status);
            return new OBWriteDomesticConsentResponse5()
                .data(data);
        }
    }

    private static class PartialDomesticPaymentResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(null, OBWriteDomesticResponse5Data.StatusEnum.PENDING)),
                Arguments.of(ofData("", OBWriteDomesticResponse5Data.StatusEnum.PENDING)),
                Arguments.of(ofData(" ", OBWriteDomesticResponse5Data.StatusEnum.PENDING)),
                Arguments.of(ofData("123", null))
            );
        }

        private OBWriteDomesticResponse5 nullData() {
            return new OBWriteDomesticResponse5();
        }

        private OBWriteDomesticResponse5 ofData(String domesticPaymentId,
            OBWriteDomesticResponse5Data.StatusEnum status) {
            OBWriteDomesticResponse5Data data = new OBWriteDomesticResponse5Data()
                .domesticPaymentId(domesticPaymentId)
                .status(status);
            return new OBWriteDomesticResponse5()
                .data(data);
        }
    }

    private static class PartialFundsConfirmationResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData())
            );
        }

        private OBWriteFundsConfirmationResponse1 nullData() {
            return new OBWriteFundsConfirmationResponse1();
        }
    }
}
