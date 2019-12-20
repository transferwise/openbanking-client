package com.transferwise.openbanking.client.api.payment.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.common.domain.CreditorAccount;
import com.transferwise.openbanking.client.api.payment.common.domain.Initiation;
import com.transferwise.openbanking.client.api.payment.common.domain.InstructedAmount;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponseData;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponseData;
import com.transferwise.openbanking.client.api.payment.common.domain.RemittanceInformation;
import com.transferwise.openbanking.client.api.payment.common.domain.Risk;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequestData;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequestData;
import com.transferwise.openbanking.client.aspsp.AspspDetails;
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
class RestPaymentClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";

    private static ObjectMapper objectMapper;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<SetupPaymentRequest, SubmitPaymentRequest> idempotencyKeyGenerator;

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

        restPaymentClient = new RestPaymentClient(oAuthClient, idempotencyKeyGenerator, restTemplate);
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
                    request.getRequestBody().get("redirect_uri").equals(aspspDetails.getTppRedirectUrl())),
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

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .apiBaseUrl("https://aspsp.co.uk")
            .tppRedirectUrl("tpp-redirect-url")
            .paymentApiMinorVersion("1")
            .build();
    }

    private SetupPaymentRequest aSetupPaymentRequest() {
        InstructedAmount instructedAmount = new InstructedAmount("1000.00", "GBP");
        CreditorAccount creditorAccount = new CreditorAccount("SortCodeAccountNumber",
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
        CreditorAccount creditorAccount = new CreditorAccount("SortCodeAccountNumber",
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
}
