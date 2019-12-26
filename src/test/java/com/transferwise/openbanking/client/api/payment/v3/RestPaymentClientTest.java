package com.transferwise.openbanking.client.api.payment.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.common.domain.CreditorAccount;
import com.transferwise.openbanking.client.api.payment.common.domain.Initiation;
import com.transferwise.openbanking.client.api.payment.common.domain.InstructedAmount;
import com.transferwise.openbanking.client.api.payment.common.domain.RemittanceInformation;
import com.transferwise.openbanking.client.api.payment.common.domain.Risk;
import com.transferwise.openbanking.client.api.payment.v3.domain.Authorisation;
import com.transferwise.openbanking.client.api.payment.v3.domain.AuthorisationType;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentData;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponseData;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentData;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.PaymentConsentStatus;
import com.transferwise.openbanking.client.api.payment.v3.domain.PaymentStatus;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.TppConfiguration;
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

    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";

    private static ObjectMapper objectMapper;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<DomesticPaymentConsentRequest, DomesticPaymentRequest> idempotencyKeyGenerator;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

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

        restPaymentClient = new RestPaymentClient(tppConfiguration,
            restTemplate,
            oAuthClient,
            idempotencyKeyGenerator,
            jwtClaimsSigner);
    }

    @Test
    void createDomesticPaymentConsent() throws Exception {
        DomesticPaymentConsentRequest domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    request.getRequestBody().get("grant_type").equals("client_credentials") &&
                        request.getRequestBody().get("scope").equals("payments")),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(domesticPaymentConsentRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        Mockito.when(jwtClaimsSigner.createDetachedSignature(domesticPaymentConsentRequest))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        DomesticPaymentConsentResponse mockPaymentConsentResponse = aDomesticPaymentConsentResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockPaymentConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getFinancialId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(domesticPaymentConsentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DomesticPaymentConsentResponse paymentConsentResponse = restPaymentClient.createDomesticPaymentConsent(
            domesticPaymentConsentRequest,
            aspspDetails);

        Assertions.assertEquals(mockPaymentConsentResponse, paymentConsentResponse);

        mockAspspServer.verify();
    }

    @Test
    void createDomesticPaymentConsentThrowsApiCallExceptionOnApiCallFailure() {
        DomesticPaymentConsentRequest domesticPaymentConsentRequest = aDomesticPaymentConsentRequest();
        AspspDetails aspspDetails = aAspspDefinition();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(DomesticPaymentConsentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payment-consents"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.createDomesticPaymentConsent(domesticPaymentConsentRequest, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPayment() throws Exception {
        DomesticPaymentRequest domesticPaymentRequest = aDomesticPaymentRequest();
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

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(domesticPaymentRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        Mockito.when(jwtClaimsSigner.createDetachedSignature(domesticPaymentRequest))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        DomesticPaymentResponse mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockDomesticPaymentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getFinancialId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(domesticPaymentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DomesticPaymentResponse domesticPaymentResponse = restPaymentClient.submitDomesticPayment(
            domesticPaymentRequest,
            authorisationCode,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticPaymentThrowsApiCallExceptionOnApiCallFailure() {
        DomesticPaymentRequest domesticPaymentRequest = aDomesticPaymentRequest();
        AspspDetails aspspDetails = aAspspDefinition();
        String authorisationCode = "authorisation-code";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(DomesticPaymentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest());

        Assertions.assertThrows(ApiCallException.class,
            () -> restPaymentClient.submitDomesticPayment(domesticPaymentRequest, authorisationCode, aspspDetails));

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
                    request.getRequestBody().get("grant_type").equals("client_credentials") &&
                        request.getRequestBody().get("scope").equals("payments")),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        DomesticPaymentResponse mockDomesticPaymentResponse = aDomesticPaymentResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockDomesticPaymentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo("https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-payments/" + domesticPaymentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getFinancialId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        DomesticPaymentResponse domesticPaymentResponse = restPaymentClient.getDomesticPayment(domesticPaymentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never()).createDetachedSignature(Mockito.any());

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

    private TppConfiguration aTppConfiguration() {
        return TppConfiguration.builder()
            .redirectUrl("tpp-redirect-url")
            .build();
    }

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .apiBaseUrl("https://aspsp.co.uk")
            .tppRedirectUrl("tpp-redirect-url")
            .paymentApiMinorVersion("1")
            .build();
    }

    private DomesticPaymentConsentRequest aDomesticPaymentConsentRequest() {
        Authorisation authorisation = Authorisation.builder()
            .authorisationType(AuthorisationType.SINGLE)
            .build();
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
        CreditorAccount creditorAccount = new CreditorAccount("SortCodeAccountNumber",
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
        return DomesticPaymentResponse.builder()
            .domesticPaymentId("domestic-payment-id")
            .status(PaymentStatus.ACCEPTED_SETTLEMENT_IN_PROCESS)
            .build();
    }

    private AccessTokenResponse aAccessTokenResponse() {
        return AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
    }
}
