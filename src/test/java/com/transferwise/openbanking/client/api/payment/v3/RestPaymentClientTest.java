package com.transferwise.openbanking.client.api.payment.v3;

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
import org.hamcrest.CoreMatchers;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.AuthorizationContextFactory.aAuthorizationContext;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RestPaymentClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";

    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";

    private static JsonConverter jsonConverter;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private MockRestServiceServer mockAspspServer;

    private RestPaymentClient restPaymentClient;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restPaymentClient = new RestPaymentClient(restTemplate,
            jsonConverter,
            oAuthClient,
            idempotencyKeyGenerator,
            jwtClaimsSigner);
    }

    @Test
    void createDomesticPaymentConsent() throws Exception {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
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
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(jsonConverter.writeValueAsString(domesticPaymentConsentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBWriteDomesticConsentResponse5 paymentConsentResponse = restPaymentClient.createDomesticPaymentConsent(
            domesticPaymentConsentRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockPaymentConsentResponse, paymentConsentResponse);

        mockAspspServer.verify();
    }

    @Test
    void createDomesticPaymentConsentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @Test
    void createDomesticPaymentConsentThrowsPaymentApiCallExceptionOnResponseWithNoContent() {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withNoContent());

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void createDomesticPaymentConsentThrowsPaymentApiCallExceptionOnPartialResponse(OBWriteDomesticConsentResponse5 response)
        throws Exception {

        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPayment() throws Exception {
        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "authorization_code".equals(request.getRequestBody().get("grant_type")) &&
                        authorizationContext.getAuthorizationCode().equals(request.getRequestBody().get("code")) &&
                        authorizationContext.getRedirectUrl().equals(request.getRequestBody().get("redirect_uri"))),
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
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(jsonConverter.writeValueAsString(domesticPaymentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBWriteDomesticResponse5 domesticPaymentResponse = restPaymentClient.submitDomesticPayment(domesticPaymentRequest,
            authorizationContext,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPaymentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBWriteDomestic2.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest());

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.submitDomesticPayment(
                domesticPaymentRequest,
                authorizationContext,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void submitDomesticPaymentThrowsPaymentApiCallExceptionOnPartialResponse(OBWriteDomesticResponse5 response)
        throws Exception {

        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBWriteDomestic2.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.submitDomesticPayment(
                domesticPaymentRequest,
                authorizationContext,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentConsent() throws Exception {
        String consentId = "consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBWriteDomesticConsentResponse5 mockDomesticPaymentConsentResponse = aDomesticPaymentConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = restPaymentClient.getDomesticPaymentConsent(
            consentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentConsentResponse, domesticPaymentConsentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentConsentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        String consentId = "consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.getDomesticPaymentConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void getDomesticPaymentConsentThrowsPaymentApiCallExceptionPartialResponse(OBWriteDomesticConsentResponse5 response)
        throws Exception {
        String consentId = "consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.getDomesticPaymentConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPayment() throws Exception {
        String domesticPaymentId = "domestic-payment-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBWriteDomesticResponse5 mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBWriteDomesticResponse5 domesticPaymentResponse = restPaymentClient.getDomesticPayment(domesticPaymentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentThrowsPaymentApiCallExceptionOnApiCallFailure() {
        String domesticPaymentId = "domestic-payment-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.getDomesticPayment(domesticPaymentId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void getDomesticPaymentThrowsPaymentApiCallExceptionPartialResponse(OBWriteDomesticResponse5 response) throws Exception {
        String domesticPaymentId = "domestic-payment-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.getDomesticPayment(domesticPaymentId, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmation() throws Exception {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "authorization_code".equals(request.getRequestBody().get("grant_type")) &&
                        authorizationContext.getAuthorizationCode().equals(request.getRequestBody().get("code")) &&
                        authorizationContext.getRedirectUrl().equals(request.getRequestBody().get("redirect_uri"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBWriteFundsConfirmationResponse1 mockFundsConfirmationResponse = aFundsConfirmationResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockFundsConfirmationResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBWriteFundsConfirmationResponse1 fundsConfirmationResponse = restPaymentClient.getFundsConfirmation(consentId,
            authorizationContext,
            aspspDetails);

        Assertions.assertEquals(mockFundsConfirmationResponse, fundsConfirmationResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmationThrowsPaymentApiCallExceptionOnApiCallFailure() {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.getFundsConfirmation(consentId, authorizationContext, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialFundsConfirmationResponses.class)
    void getFundsConfirmationThrowsPaymentApiCallExceptionPartialResponse(OBWriteFundsConfirmationResponse1 response)
        throws Exception {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(PaymentApiCallException.class,
            () -> restPaymentClient.getFundsConfirmation(consentId, authorizationContext, aspspDetails));

        mockAspspServer.verify();
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
