package com.transferwise.openbanking.client.api.vrp;

import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBActiveOrHistoricCurrencyAndAmount;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBCashAccountCreditor3;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPDetails;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPDetailsData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPDetailsDataPaymentStatus;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPInitiation;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPInitiationRemittanceInformation;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPInstruction;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPRequestData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPResponseData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBExternalAccountIdentification4Code;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBExternalLocalInstrument1Code;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBRisk1;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPAuthenticationMethods;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPRemittanceInformation;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.json.JacksonJsonConverter;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
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

import java.util.List;
import java.util.stream.Stream;

import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.AspspDetailsFactory.aAspspDetails;
import static com.transferwise.openbanking.client.test.factory.AuthorizationContextFactory.aAuthorizationContext;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RestVrpClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";
    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";
    public static final String DOMESTIC_VRP_URL = "https://aspsp.co.uk/open-banking/v3.1/vrp/domestic-vrps";

    private static JsonConverter jsonConverter;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<OBDomesticVRPConsentRequest, OBDomesticVRPRequest> idempotencyKeyGenerator;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private MockRestServiceServer mockAspspServer;

    private RestVrpClient restVrpClient;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restVrpClient = new RestVrpClient(
            restTemplate,
            jsonConverter,
            oAuthClient,
            idempotencyKeyGenerator,
            jwtClaimsSigner);
    }

    @Test
    void submitDomesticVrp() {
        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        AspspDetails aspspDetails = aAspspDetails();
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

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(domesticVrpRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        Mockito.when(
            jwtClaimsSigner.createDetachedSignature(
                domesticVrpRequest,
                aspspDetails,
                softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBDomesticVRPResponse mockDomesticPaymentResponse = aDomesticVrpResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticPaymentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(jsonConverter.writeValueAsString(domesticVrpRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBDomesticVRPResponse domesticPaymentResponse = restVrpClient.submitDomesticVrp(domesticVrpRequest,
            authorizationContext,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticVrpThrowsApiCallExceptionOnApiCallFailure() {
        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        AspspDetails aspspDetails = aAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBDomesticVRPRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest());

        Assertions.assertThrows(ApiCallException.class,
            () -> restVrpClient.submitDomesticVrp(
                domesticVrpRequest,
                authorizationContext,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticVrpResponses.class)
    void submitDomesticVrpThrowsApiCallExceptionOnPartialResponse(OBDomesticVRPResponse response) {

        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        AspspDetails aspspDetails = aAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        AuthorizationContext authorizationContext = aAuthorizationContext();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBDomesticVRPRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restVrpClient.submitDomesticVrp(
                domesticVrpRequest,
                authorizationContext,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrp() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = aAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBDomesticVRPResponse mockDomesticVrpResponse = aDomesticVrpResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBDomesticVRPResponse domesticVrpResponse = restVrpClient.getDomesticVrp(vrpId, aspspDetails);

        Assertions.assertEquals(mockDomesticVrpResponse, domesticVrpResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrpThrowsApiCallExceptionOnApiCallFailure() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = aAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restVrpClient.getDomesticVrp(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticVrpResponses.class)
    void getDomesticVrpThrowsApiCallExceptionPartialResponse(OBDomesticVRPResponse response) {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = aAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restVrpClient.getDomesticVrp(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrpDetails() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = aAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBDomesticVRPDetails mockDomesticVrpDetailsResponse = aDomesticVrpDetailsResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpDetailsResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBDomesticVRPDetails domesticVrpDetailsResponse = restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails);

        Assertions.assertEquals(mockDomesticVrpDetailsResponse, domesticVrpDetailsResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrpDetailsThrowsApiCallExceptionOnApiCallFailure() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = aAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticVrpDetailsResponses.class)
    void getDomesticVrpDetailsThrowsApiCallExceptionPartialResponse(OBDomesticVRPDetails response) {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = aAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    private OBDomesticVRPRequest aDomesticVrpRequest() {
        OBActiveOrHistoricCurrencyAndAmount amount = new OBActiveOrHistoricCurrencyAndAmount()
            .amount("5000.00")
            .currency("GBP");
        OBCashAccountCreditor3 creditorAccount = new OBCashAccountCreditor3()
            .schemeName(OBExternalAccountIdentification4Code.SORTCODEACCOUNTNUMBER)
            .identification("231470123456")
            .name("Wise Payments Ltd");
        OBVRPRemittanceInformation remittanceInformation = new OBVRPRemittanceInformation()
            .reference("reference");
        OBDomesticVRPInstruction instruction = new OBDomesticVRPInstruction()
            .instructionIdentification("transfer123")
            .endToEndIdentification("uniqueuuid")
            .remittanceInformation(remittanceInformation)
            .localInstrument(OBExternalLocalInstrument1Code.FPS)
            .instructedAmount(amount)
            .creditorAccount(creditorAccount);
        OBDomesticVRPInitiationRemittanceInformation initiationRemittanceInformation = new OBDomesticVRPInitiationRemittanceInformation()
            .reference("reference");
        OBDomesticVRPInitiation initiation = new OBDomesticVRPInitiation()
            .creditorAccount(creditorAccount)
            .remittanceInformation(initiationRemittanceInformation);
        OBDomesticVRPRequestData data = new OBDomesticVRPRequestData()
            .consentId("vrp-consent-id")
            .psUAuthenticationMethod(OBVRPAuthenticationMethods.SCA)
            .initiation(initiation)
            .instruction(instruction);
        OBRisk1 risk = new OBRisk1()
            .paymentContextCode(OBRisk1.PaymentContextCodeEnum.OTHER);
        return new OBDomesticVRPRequest()
            .data(data)
            .risk(risk);
    }

    private OBDomesticVRPResponse aDomesticVrpResponse() {
        OBDomesticVRPResponseData data = new OBDomesticVRPResponseData()
            .consentId("vrp-consent-id")
            .domesticVRPId("vrp-id")
            .status(OBDomesticVRPResponseData.StatusEnum.ACCEPTEDSETTLEMENTCOMPLETED);
        return new OBDomesticVRPResponse()
            .data(data);
    }

    private OBDomesticVRPDetails aDomesticVrpDetailsResponse() {
        OBDomesticVRPDetailsDataPaymentStatus status = new OBDomesticVRPDetailsDataPaymentStatus()
            .status(OBDomesticVRPDetailsDataPaymentStatus.StatusEnum.ACCEPTED)
            .paymentTransactionId("567");
        OBDomesticVRPDetailsData data = new OBDomesticVRPDetailsData()
            .paymentStatus(List.of(status));
        return new OBDomesticVRPDetails()
            .data(data);
    }

    private static class PartialDomesticVrpResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData("123", "567", null)),
                Arguments.of(ofData(null, "567", OBDomesticVRPResponseData.StatusEnum.ACCEPTEDSETTLEMENTINPROCESS)),
                Arguments.of(ofData("", "567", OBDomesticVRPResponseData.StatusEnum.PENDING)),
                Arguments.of(ofData(" ", "567", OBDomesticVRPResponseData.StatusEnum.ACCEPTEDSETTLEMENTINPROCESS)),
                Arguments.of(ofData("123", null, OBDomesticVRPResponseData.StatusEnum.PENDING)),
                Arguments.of(ofData("123", "", OBDomesticVRPResponseData.StatusEnum.ACCEPTEDSETTLEMENTCOMPLETED)),
                Arguments.of(ofData("123", " ", OBDomesticVRPResponseData.StatusEnum.PENDING))
            );
        }

        private OBDomesticVRPResponse nullData() {
            return new OBDomesticVRPResponse();
        }

        private OBDomesticVRPResponse ofData(
            String consentId,
            String domesticVrpId,
            OBDomesticVRPResponseData.StatusEnum status
        ) {
            OBDomesticVRPResponseData data = new OBDomesticVRPResponseData()
                .consentId(consentId)
                .domesticVRPId(domesticVrpId)
                .status(status);
            return new OBDomesticVRPResponse()
                .data(data);
        }
    }

    private static class PartialDomesticVrpDetailsResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(List.of()))
            );
        }

        private OBDomesticVRPDetails nullData() {
            return new OBDomesticVRPDetails();
        }

        private OBDomesticVRPDetails ofData(List<OBDomesticVRPDetailsDataPaymentStatus> statusList) {
            OBDomesticVRPDetailsData data = new OBDomesticVRPDetailsData()
                .paymentStatus(statusList);
            return new OBDomesticVRPDetails()
                .data(data);
        }
    }

}
