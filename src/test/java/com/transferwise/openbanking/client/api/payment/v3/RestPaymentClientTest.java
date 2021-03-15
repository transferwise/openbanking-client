package com.transferwise.openbanking.client.api.payment.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.api.payment.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.common.domain.InstructedAmount;
import com.transferwise.openbanking.client.api.payment.common.domain.RemittanceInformation;
import com.transferwise.openbanking.client.api.payment.common.domain.Risk;
import com.transferwise.openbanking.client.api.payment.v3.domain.AccountIdentificationCode;
import com.transferwise.openbanking.client.api.payment.v3.domain.Authorisation;
import com.transferwise.openbanking.client.api.payment.v3.domain.AuthorisationType;
import com.transferwise.openbanking.client.api.payment.v3.domain.CreditorAccount;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentData;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponseData;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentData;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentResponseData;
import com.transferwise.openbanking.client.api.payment.v3.domain.FundsAvailableResult;
import com.transferwise.openbanking.client.api.payment.v3.domain.FundsConfirmationResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.FundsConfirmationResponseData;
import com.transferwise.openbanking.client.api.payment.v3.domain.Initiation;
import com.transferwise.openbanking.client.api.payment.v3.domain.PaymentConsentStatus;
import com.transferwise.openbanking.client.api.payment.v3.domain.PaymentStatus;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.test.TestAspspDetails;
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

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RestPaymentClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";

    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";

    private static ObjectMapper objectMapper;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<DomesticPaymentConsentRequest, DomesticPaymentRequest> idempotencyKeyGenerator;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private MockRestServiceServer mockAspspServer;

    private RestPaymentClient restPaymentClient;

    @BeforeAll
    static void initAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restPaymentClient = new RestPaymentClient(restTemplate,
            oAuthClient,
            idempotencyKeyGenerator,
            jwtClaimsSigner);
    }

    @Test
    void createDomesticPaymentConsent() throws Exception {
        DomesticPaymentConsentRequest domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
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

        DomesticPaymentConsentResponse mockPaymentConsentResponse = aDomesticPaymentConsentResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockPaymentConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(domesticPaymentConsentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DomesticPaymentConsentResponse paymentConsentResponse = restPaymentClient.createDomesticPaymentConsent(
            domesticPaymentConsentRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockPaymentConsentResponse, paymentConsentResponse);

        mockAspspServer.verify();
    }

    @Test
    void createDomesticPaymentConsentThrowsApiCallExceptionOnApiCallFailure() {
        DomesticPaymentConsentRequest domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(DomesticPaymentConsentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void createDomesticPaymentConsentThrowsApiCallExceptionOnPartialResponse(DomesticPaymentConsentResponse response)
        throws Exception{

        DomesticPaymentConsentRequest domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(DomesticPaymentConsentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPayment() throws Exception {
        DomesticPaymentRequest domesticPaymentRequest = aDomesticPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
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

        DomesticPaymentResponse mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockDomesticPaymentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(domesticPaymentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DomesticPaymentResponse domesticPaymentResponse = restPaymentClient.submitDomesticPayment(
            domesticPaymentRequest,
            authorizationContext,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPaymentThrowsApiCallExceptionOnApiCallFailure() {
        DomesticPaymentRequest domesticPaymentRequest = aDomesticPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(DomesticPaymentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.submitDomesticPayment(
                domesticPaymentRequest,
                authorizationContext,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void submitDomesticPaymentThrowsApiCallExceptionOnPartialResponse(DomesticPaymentResponse response)
        throws Exception {

        DomesticPaymentRequest domesticPaymentRequest = aDomesticPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(DomesticPaymentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
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
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        DomesticPaymentConsentResponse mockDomesticPaymentConsentResponse = aDomesticPaymentConsentResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockDomesticPaymentConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DomesticPaymentConsentResponse domesticPaymentConsentResponse = restPaymentClient.getDomesticPaymentConsent(
            consentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentConsentResponse, domesticPaymentConsentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentConsentThrowsApiCallExceptionOnApiCallFailure() {
        String consentId = "consent-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getDomesticPaymentConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void getDomesticPaymentConsentThrowsApiCallExceptionPartialResponse(DomesticPaymentConsentResponse response)
        throws Exception {
        String consentId = "consent-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getDomesticPaymentConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPayment() throws Exception {
        String domesticPaymentId = "domestic-payment-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        DomesticPaymentResponse mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockDomesticPaymentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DomesticPaymentResponse domesticPaymentResponse = restPaymentClient.getDomesticPayment(domesticPaymentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentThrowsApiCallExceptionOnApiCallFailure() {
        String domesticPaymentId = "domestic-payment-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getDomesticPayment(domesticPaymentId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void getDomesticPaymentThrowsApiCallExceptionPartialResponse(DomesticPaymentResponse response) throws Exception {
        String domesticPaymentId = "domestic-payment-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getDomesticPayment(domesticPaymentId, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmation() throws Exception {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "authorization_code".equals(request.getRequestBody().get("grant_type")) &&
                        authorizationContext.getAuthorizationCode().equals(request.getRequestBody().get("code")) &&
                        authorizationContext.getRedirectUrl().equals(request.getRequestBody().get("redirect_uri"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        FundsConfirmationResponse mockFundsConfirmationResponse = aFundsConfirmationResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockFundsConfirmationResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        FundsConfirmationResponse fundsConfirmationResponse = restPaymentClient.getFundsConfirmation(consentId,
            authorizationContext,
            aspspDetails);

        Assertions.assertEquals(mockFundsConfirmationResponse, fundsConfirmationResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmationThrowsApiCallExceptionOnApiCallFailure() {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getFundsConfirmation(consentId, authorizationContext, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialFundsConfirmationResponses.class)
    void getFundsConfirmationThrowsApiCallExceptionPartialResponse(FundsConfirmationResponse response) throws Exception {
        String consentId = "consent-id";
        AuthorizationContext authorizationContext = aAuthorizationContext();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getFundsConfirmation(consentId, authorizationContext, aspspDetails));

        mockAspspServer.verify();
    }

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .apiBaseUrl("https://aspsp.co.uk")
            .paymentApiMinorVersion("1")
            .build();
    }

    private SoftwareStatementDetails aSoftwareStatementDetails() {
        return SoftwareStatementDetails.builder()
            .build();
    }

    private DomesticPaymentConsentRequest aDomesticPaymentConsentRequest() {
        Authorisation authorisation = Authorisation.builder()
            .authorisationType(AuthorisationType.SINGLE)
            .build();
        InstructedAmount instructedAmount = new InstructedAmount("1000.00", "GBP");
        CreditorAccount creditorAccount = new CreditorAccount(AccountIdentificationCode.SORT_CODE_ACCOUNT_NUMBER,
            "112233123456",
            "TransferWise Ltd");
        RemittanceInformation remittanceInformation = new RemittanceInformation("reference");
        Initiation initiation = new Initiation("instruction-identification",
            "end-to-end-identification",
            instructedAmount,
            creditorAccount,
            remittanceInformation);
        DomesticPaymentConsentData data = DomesticPaymentConsentData.builder()
            .authorisation(authorisation)
            .initiation(initiation)
            .build();
        Risk risk = new Risk("Other");
        return DomesticPaymentConsentRequest.builder()
            .data(data)
            .risk(risk)
            .build();
    }

    private DomesticPaymentRequest aDomesticPaymentRequest() {
        InstructedAmount instructedAmount = new InstructedAmount("1000.00", "GBP");
        CreditorAccount creditorAccount = new CreditorAccount(AccountIdentificationCode.SORT_CODE_ACCOUNT_NUMBER,
            "112233123456",
            "TransferWise Ltd");
        RemittanceInformation remittanceInformation = new RemittanceInformation("reference");
        Initiation initiation = new Initiation("instruction-identification",
            "end-to-end-identification",
            instructedAmount,
            creditorAccount,
            remittanceInformation);
        DomesticPaymentData data = DomesticPaymentData.builder()
            .consentId("consent-id")
            .initiation(initiation)
            .build();
        Risk risk = new Risk("Other");
        return DomesticPaymentRequest.builder()
            .data(data)
            .risk(risk)
            .build();
    }

    private DomesticPaymentConsentResponse aDomesticPaymentConsentResponse() {
        DomesticPaymentConsentResponseData data = DomesticPaymentConsentResponseData.builder()
            .consentId("consent-id")
            .status(PaymentConsentStatus.AUTHORISED)
            .build();
        return DomesticPaymentConsentResponse.builder()
            .data(data)
            .build();
    }

    private DomesticPaymentResponse aDomesticPaymentResponse() {
        DomesticPaymentResponseData data = DomesticPaymentResponseData.builder()
            .domesticPaymentId("domestic-payment-id")
            .status(PaymentStatus.ACCEPTED_SETTLEMENT_IN_PROCESS)
            .build();
        return DomesticPaymentResponse.builder()
            .data(data)
            .build();
    }

    private AccessTokenResponse aAccessTokenResponse() {
        return AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
    }

    private FundsConfirmationResponse aFundsConfirmationResponse() {
        FundsAvailableResult fundsAvailableResult = FundsAvailableResult.builder()
            .fundsAvailable(true)
            .build();
        FundsConfirmationResponseData data = FundsConfirmationResponseData.builder()
            .fundsAvailableResult(fundsAvailableResult)
            .build();
        return FundsConfirmationResponse.builder()
            .data(data)
            .build();
    }

    private AuthorizationContext aAuthorizationContext() {
        return AuthorizationContext.builder()
            .authorizationCode("authorisation-code")
            .redirectUrl("https://tpp.co.uk")
            .build();
    }

    private static class PartialDomesticPaymentConsentResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(null, PaymentConsentStatus.CONSUMED)),
                Arguments.of(ofData("", PaymentConsentStatus.CONSUMED)),
                Arguments.of(ofData(" ", PaymentConsentStatus.CONSUMED)),
                Arguments.of(ofData("123", null))
            );
        }

        private DomesticPaymentConsentResponse nullData() {
            return new DomesticPaymentConsentResponse();
        }

        private DomesticPaymentConsentResponse ofData(String consentId, PaymentConsentStatus status) {
            DomesticPaymentConsentResponseData data = DomesticPaymentConsentResponseData.builder()
                .consentId(consentId)
                .status(status)
                .build();
            return DomesticPaymentConsentResponse.builder()
                .data(data)
                .build();
        }
    }

    private static class PartialDomesticPaymentResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(null, PaymentStatus.PENDING)),
                Arguments.of(ofData("", PaymentStatus.PENDING)),
                Arguments.of(ofData(" ", PaymentStatus.PENDING)),
                Arguments.of(ofData("123", null))
            );
        }

        private DomesticPaymentResponse nullData() {
            return new DomesticPaymentResponse();
        }

        private DomesticPaymentResponse ofData(String domesticPaymentId, PaymentStatus status) {
            DomesticPaymentResponseData data = DomesticPaymentResponseData.builder()
                .domesticPaymentId(domesticPaymentId)
                .status(status)
                .build();
            return DomesticPaymentResponse.builder()
                .data(data)
                .build();
        }
    }

    private static class PartialFundsConfirmationResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData())
            );
        }

        private FundsConfirmationResponse nullData() {
            return new FundsConfirmationResponse();
        }
    }
}
