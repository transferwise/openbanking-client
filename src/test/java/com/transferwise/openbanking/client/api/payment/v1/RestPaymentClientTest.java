package com.transferwise.openbanking.client.api.payment.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.common.domain.InstructedAmount;
import com.transferwise.openbanking.client.api.payment.common.domain.RemittanceInformation;
import com.transferwise.openbanking.client.api.payment.common.domain.Risk;
import com.transferwise.openbanking.client.api.payment.v1.domain.AccountIdentificationCode;
import com.transferwise.openbanking.client.api.payment.v1.domain.CreditorAccount;
import com.transferwise.openbanking.client.api.payment.v1.domain.Initiation;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponseData;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponseData;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequestData;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequestData;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.TppConfiguration;
import com.transferwise.openbanking.client.error.ApiCallException;
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

    private static ObjectMapper objectMapper;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<SetupPaymentRequest, SubmitPaymentRequest> idempotencyKeyGenerator;

    private TppConfiguration tppConfiguration;

    private MockRestServiceServer mockAspspServer;

    private RestPaymentClient restPaymentClient;

    @BeforeAll
    static void initAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        tppConfiguration = aTppConfiguration();

        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restPaymentClient = new RestPaymentClient(tppConfiguration, restTemplate, oAuthClient, idempotencyKeyGenerator);
    }

    @Test
    void setupPayment() throws Exception {
        SetupPaymentRequest setupPaymentRequest = aSetupPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    request.getRequestBody().get("grant_type").equals("client_credentials") &&
                        request.getRequestBody().get("scope").equals("payments")),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(setupPaymentRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        PaymentSetupResponse mockSetupResponse = aPaymentSetupResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockSetupResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getFinancialId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(setupPaymentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        PaymentSetupResponse setupResponse = restPaymentClient.setupPayment(setupPaymentRequest, aspspDetails);

        Assertions.assertEquals(mockSetupResponse, setupResponse);

        mockAspspServer.verify();
    }

    @Test
    void setupPaymentThrowsApiCallExceptionOnApiCallFailure() {
        SetupPaymentRequest setupPaymentRequest = aSetupPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(SetupPaymentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.setupPayment(setupPaymentRequest, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialPaymentSetupResponses.class)
    void setupPaymentThrowsApiCallExceptionOnPartialResponse(PaymentSetupResponse response) throws Exception {

        SetupPaymentRequest setupPaymentRequest = aSetupPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(SetupPaymentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.setupPayment(setupPaymentRequest, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void submitPayment() throws Exception {
        SubmitPaymentRequest submitPaymentRequest = aSubmitPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        String authorisationCode = "authorisation-code";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    request.getRequestBody().get("grant_type").equals("authorization_code") &&
                    request.getRequestBody().get("code").equals(authorisationCode) &&
                    request.getRequestBody().get("redirect_uri").equals(tppConfiguration.getRedirectUrl())),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(submitPaymentRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        PaymentSubmissionResponse mockPaymentSubmissionResponse = aPaymentSubmissionResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockPaymentSubmissionResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payment-submissions"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getFinancialId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(submitPaymentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        PaymentSubmissionResponse paymentSubmissionResponse = restPaymentClient.submitPayment(submitPaymentRequest,
            authorisationCode,
            aspspDetails);

        Assertions.assertEquals(mockPaymentSubmissionResponse, paymentSubmissionResponse);

        mockAspspServer.verify();
    }

    @Test
    void submitPaymentThrowsApiCallExceptionOnApiCallFailure() {
        SubmitPaymentRequest submitPaymentRequest = aSubmitPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        String authorisationCode = "authorisation-code";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(SubmitPaymentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payment-submissions"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.submitPayment(submitPaymentRequest, authorisationCode, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialPaymentSubmissionResponses.class)
    void submitPaymentThrowsApiCallExceptionOnPartialResponse(PaymentSubmissionResponse response) throws Exception {
        SubmitPaymentRequest submitPaymentRequest = aSubmitPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        String authorisationCode = "authorisation-code";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(SubmitPaymentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payment-submissions"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.submitPayment(submitPaymentRequest, authorisationCode, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void getPaymentSubmission() throws Exception {
        String paymentSubmissionId = "payment-submission-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    request.getRequestBody().get("grant_type").equals("client_credentials") &&
                        request.getRequestBody().get("scope").equals("payments")),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        PaymentSubmissionResponse mockPaymentSubmissionResponse = aPaymentSubmissionResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockPaymentSubmissionResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payment-submissions/" + paymentSubmissionId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getFinancialId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        PaymentSubmissionResponse paymentSubmissionResponse = restPaymentClient.getPaymentSubmission(
            paymentSubmissionId,
            aspspDetails);

        Assertions.assertEquals(mockPaymentSubmissionResponse, paymentSubmissionResponse);

        mockAspspServer.verify();
    }

    @Test
    void getPaymentSubmissionThrowsApiCallExceptionOnApiCallFailure() {
        String paymentSubmissionId = "payment-submission-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payment-submissions/" + paymentSubmissionId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getPaymentSubmission(paymentSubmissionId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialPaymentSubmissionResponses.class)
    void getPaymentSubmissionThrowsApiCallExceptionOnPartialResponse(PaymentSubmissionResponse response)
        throws Exception {

        String paymentSubmissionId = "payment-submission-id";
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = objectMapper.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v1.1/payment-submissions/" + paymentSubmissionId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.getPaymentSubmission(paymentSubmissionId, aspspDetails));

        mockAspspServer.verify();
    }

    private TppConfiguration aTppConfiguration() {
        return TppConfiguration.builder()
            .redirectUrl("tpp-redirect-url")
            .build();
    }

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .apiBaseUrl("https://aspsp.co.uk")
            .paymentApiMinorVersion("1")
            .build();
    }

    private SetupPaymentRequest aSetupPaymentRequest() {
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
        SetupPaymentRequestData setupPaymentRequestData = new SetupPaymentRequestData(initiation);
        Risk risk = new Risk("Other");
        return new SetupPaymentRequest(setupPaymentRequestData, risk);
    }

    private SubmitPaymentRequest aSubmitPaymentRequest() {
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
        SubmitPaymentRequestData submitPaymentRequestData = new SubmitPaymentRequestData(initiation, "payment-id");
        Risk risk = new Risk("Other");
        return new SubmitPaymentRequest(submitPaymentRequestData, risk);
    }

    private PaymentSetupResponse aPaymentSetupResponse() {
        PaymentSetupResponseData data = new PaymentSetupResponseData();
        data.setPaymentId("payment-id");
        data.setStatus("PENDING");

        PaymentSetupResponse paymentSetupResponse = new PaymentSetupResponse();
        paymentSetupResponse.setData(data);
        return paymentSetupResponse;
    }

    private PaymentSubmissionResponse aPaymentSubmissionResponse() {
        PaymentSubmissionResponseData data = new PaymentSubmissionResponseData();
        data.setPaymentId("payment-id");
        data.setPaymentSubmissionId("payment-submission-id");
        data.setStatus("PENDING");

        PaymentSubmissionResponse paymentSubmissionResponse = new PaymentSubmissionResponse();
        paymentSubmissionResponse.setData(data);
        return paymentSubmissionResponse;
    }

    private AccessTokenResponse aAccessTokenResponse() {
        return AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
    }

    private static class PartialPaymentSetupResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(null)),
                Arguments.of(ofData("")),
                Arguments.of(ofData(" "))
            );
        }

        private PaymentSetupResponse nullData() {
            return new PaymentSetupResponse();
        }

        private PaymentSetupResponse ofData(String paymentId) {
            PaymentSetupResponseData data = new PaymentSetupResponseData();
            data.setPaymentId(paymentId);
            PaymentSetupResponse response = new PaymentSetupResponse();
            response.setData(data);
            return response;
        }
    }

    private static class PartialPaymentSubmissionResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(null, "123")),
                Arguments.of(ofData("", "123")),
                Arguments.of(ofData(" ", "123")),
                Arguments.of(ofData("123", null)),
                Arguments.of(ofData("123", "")),
                Arguments.of(ofData("123", " "))
            );
        }

        private PaymentSubmissionResponse nullData() {
            return new PaymentSubmissionResponse();
        }

        private PaymentSubmissionResponse ofData(String paymentId, String paymentSubmissionId) {
            PaymentSubmissionResponseData data = new PaymentSubmissionResponseData();
            data.setPaymentId(paymentId);
            data.setPaymentSubmissionId(paymentSubmissionId);
            PaymentSubmissionResponse response = new PaymentSubmissionResponse();
            response.setData(data);
            return response;
        }
    }
}
