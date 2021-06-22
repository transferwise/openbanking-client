package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.OBErrorResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.OBExternalAccountIdentification4Code;
import com.transferwise.openbanking.client.api.payment.v3.model.OBRisk1;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2Data;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2DataInitiation;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2DataInitiationCreditorAccount;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2DataInitiationInstructedAmount;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2DataInitiationRemittanceInformation;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsent4Data;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsent4DataAuthorisation;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsentResponse5Data;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticResponse5Data;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteFundsConfirmationResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteFundsConfirmationResponse1Data;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteFundsConfirmationResponse1DataFundsAvailableResult;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.json.JacksonJsonConverter;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RestPaymentClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";

    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";

    private static JsonConverter jsonConverter;

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
            idempotencyKeyGenerator,
            jwtClaimsSigner);
    }

    @Test
    void createDomesticPaymentConsentReturnsSuccessResponseOnApiCallSuccess() {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

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
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + clientCredentialsToken))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(jsonConverter.writeValueAsString(domesticPaymentConsentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> apiResponse =
            restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                clientCredentialsToken,
                aspspDetails,
                softwareStatementDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockPaymentConsentResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        mockAspspServer.verify();
    }

    @Test
    void createDomesticPaymentConsentReturnsFailureResponseOnApiCallFailure() {
        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError().body("internal server error"));

        ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> apiResponse =
            restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                clientCredentialsToken,
                aspspDetails,
                softwareStatementDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(500, apiResponse.getStatusCode());
        Assertions.assertEquals("internal server error", apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof HttpServerErrorException.InternalServerError);
        Assertions.assertFalse(apiResponse.isClientErrorResponse());
        Assertions.assertTrue(apiResponse.isServerErrorResponse());

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void createDomesticPaymentConsentReturnsFailureResponseOnPartialResponse(OBWriteDomesticConsentResponse5 response) {

        OBWriteDomesticConsent4 domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBWriteDomesticConsent4.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> apiResponse =
            restPaymentClient.createDomesticPaymentConsent(
                domesticPaymentConsentRequest,
                clientCredentialsToken,
                aspspDetails,
                softwareStatementDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(200, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof ApiCallException);

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPaymentReturnsSuccessResponseOnApiCallSuccess() {
        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        String authorizationCodeToken = "authorization-code-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

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
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + authorizationCodeToken))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(jsonConverter.writeValueAsString(domesticPaymentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> apiResponse = restPaymentClient.submitDomesticPayment(
            domesticPaymentRequest,
            authorizationCodeToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockDomesticPaymentResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPaymentReturnsFailureResponseOnApiCallFailure() {
        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        String authorizationCodeToken = "authorization-code-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBWriteDomestic2.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        OBErrorResponse1 mockErrorResponse = aErrorResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockErrorResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest().body(jsonResponse));

        ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> apiResponse = restPaymentClient.submitDomesticPayment(
                domesticPaymentRequest,
                authorizationCodeToken,
                aspspDetails,
                softwareStatementDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(400, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertEquals(mockErrorResponse, apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof HttpClientErrorException.BadRequest);
        Assertions.assertTrue(apiResponse.isClientErrorResponse());
        Assertions.assertFalse(apiResponse.isServerErrorResponse());

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void submitDomesticPaymentReturnsFailureResponseOnPartialResponse(OBWriteDomesticResponse5 response) {

        OBWriteDomestic2 domesticPaymentRequest = aDomesticPaymentRequest();
        String authorizationCodeToken = "authorization-code-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBWriteDomestic2.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> apiResponse = restPaymentClient.submitDomesticPayment(
            domesticPaymentRequest,
            authorizationCodeToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(200, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof ApiCallException);

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentConsentReturnsSuccessResponseOnApiCallSuccess() {
        String consentId = "consent-id";
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();

        OBWriteDomesticConsentResponse5 mockDomesticPaymentConsentResponse = aDomesticPaymentConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + clientCredentialsToken))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> apiResponse =
            restPaymentClient.getDomesticPaymentConsent(
                consentId,
                clientCredentialsToken,
                aspspDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockDomesticPaymentConsentResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentConsentReturnsFailureResponseOnApiCallFailure() {
        String consentId = "consent-id";
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError().body("<html>internal server error</html>"));

        ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> apiResponse =
            restPaymentClient.getDomesticPaymentConsent(
                consentId,
                clientCredentialsToken,
                aspspDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(500, apiResponse.getStatusCode());
        Assertions.assertEquals("<html>internal server error</html>", apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof HttpServerErrorException.InternalServerError);

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentConsentResponses.class)
    void getDomesticPaymentConsentReturnsFailureResponsePartialResponse(OBWriteDomesticConsentResponse5 response) {
        String consentId = "consent-id";
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> apiResponse =
            restPaymentClient.getDomesticPaymentConsent(
                consentId,
                clientCredentialsToken,
                aspspDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(200, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof ApiCallException);

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentReturnsSuccessResponseOnApiCallSuccess() {
        String domesticPaymentId = "domestic-payment-id";
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();

        OBWriteDomesticResponse5 mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + clientCredentialsToken))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> apiResponse = restPaymentClient.getDomesticPayment(
            domesticPaymentId,
            clientCredentialsToken,
            aspspDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockDomesticPaymentResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticPaymentReturnsFailureResponseOnApiCallFailure() {
        String domesticPaymentId = "domestic-payment-id";
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> apiResponse = restPaymentClient.getDomesticPayment(
            domesticPaymentId,
            clientCredentialsToken,
            aspspDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(500, apiResponse.getStatusCode());
        Assertions.assertEquals("", apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof HttpServerErrorException.InternalServerError);

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticPaymentResponses.class)
    void getDomesticPaymentReturnsFailureResponsePartialResponse(OBWriteDomesticResponse5 response) {
        String domesticPaymentId = "domestic-payment-id";
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> apiResponse = restPaymentClient.getDomesticPayment(
            domesticPaymentId,
            clientCredentialsToken,
            aspspDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(200, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof ApiCallException);

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmationReturnsSuccessResponseOnApiCallSuccess() {
        String consentId = "consent-id";
        String authorizationCodeToken = "authorization-code-token";
        AspspDetails aspspDetails = aAspspDefinition();

        OBWriteFundsConfirmationResponse1 mockFundsConfirmationResponse = aFundsConfirmationResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockFundsConfirmationResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + authorizationCodeToken))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteFundsConfirmationResponse1, OBErrorResponse1> apiResponse =
            restPaymentClient.getFundsConfirmation(consentId,
                authorizationCodeToken,
                aspspDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockFundsConfirmationResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmationReturnsFailureResponseOnApiCallFailure() {
        String consentId = "consent-id";
        String authorizationCodeToken = "authorization-code-token";
        AspspDetails aspspDetails = aAspspDefinition();

        OBErrorResponse1 mockErrorResponse = aErrorResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockErrorResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError().body(jsonResponse));

        ApiResponse<OBWriteFundsConfirmationResponse1, OBErrorResponse1> apiResponse =
            restPaymentClient.getFundsConfirmation(consentId,
                authorizationCodeToken,
                aspspDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(500, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertEquals(mockErrorResponse, apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof HttpServerErrorException.InternalServerError);

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialFundsConfirmationResponses.class)
    void getFundsConfirmationReturnsFailureResponsePartialResponse(OBWriteFundsConfirmationResponse1 response) {
        String consentId = "consent-id";
        String authorizationCodeToken = "authorization-code-token";
        AspspDetails aspspDetails = aAspspDefinition();

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<OBWriteFundsConfirmationResponse1, OBErrorResponse1> apiResponse =
            restPaymentClient.getFundsConfirmation(consentId,
                authorizationCodeToken,
                aspspDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(200, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof ApiCallException);

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

    private OBErrorResponse1 aErrorResponse() {
        return new OBErrorResponse1()
            .code("403")
            .message("Forbidden: this payment exceeds the daily payment limit");
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
