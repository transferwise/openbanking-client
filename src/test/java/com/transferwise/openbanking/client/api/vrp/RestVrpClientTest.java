package com.transferwise.openbanking.client.api.vrp;

import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBActiveOrHistoricCurrencyAndAmount;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBCashAccountCreditor3;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBCashAccountDebtorWithName;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequestData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponseData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPControlParameters;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPControlParametersPeriodicLimits;
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
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBPAFundsAvailableResult1;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBRisk1;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPAuthenticationMethods;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPConsentType;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationRequestData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPFundsConfirmationResponseData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBVRPRemittanceInformation;
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
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RestVrpClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";
    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";
    public static final String DOMESTIC_VRP_CONSENTS_URL = "https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-vrp-consents";
    public static final String DOMESTIC_VRP_URL = "https://aspsp.co.uk/open-banking/v3.1/pisp/domestic-vrps";

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
    void createDomesticVrpConsent() {
        OBDomesticVRPConsentRequest domesticVRPConsentRequest = aDomesticVrpConsentRequest();
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

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(domesticVRPConsentRequest))
            .thenReturn(IDEMPOTENCY_KEY);

        Mockito.when(
            jwtClaimsSigner.createDetachedSignature(
                domesticVRPConsentRequest,
                aspspDetails,
                softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBDomesticVRPConsentResponse mockDomesticVrpConsentResponse = aDomesticVrpConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(jsonConverter.writeValueAsString(domesticVRPConsentRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBDomesticVRPConsentResponse domesticVrpConsentResponse = restVrpClient.createDomesticVrpConsent(
            domesticVRPConsentRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticVrpConsentResponse, domesticVrpConsentResponse);

        mockAspspServer.verify();
    }

    @Test
    void createDomesticVrpConsentThrowsVrpApiCallExceptionOnApiCallFailure() {
        OBDomesticVRPConsentRequest domesticVRPConsentRequest = aDomesticVrpConsentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBDomesticVRPConsentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.createDomesticVrpConsent(
                domesticVRPConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }


    @ParameterizedTest
    @ArgumentsSource(PartialDomesticVrpConsentResponses.class)
    void createDomesticVrpConsentThrowsVrpApiCallExceptionOnPartialResponse(OBDomesticVRPConsentResponse response) {
        OBDomesticVRPConsentRequest domesticVRPConsentRequest = aDomesticVrpConsentRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBDomesticVRPConsentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.createDomesticVrpConsent(
                domesticVRPConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmation() {
        String consentId = "vrp-consent-id";
        String accessToken = "access-token";
        OBVRPFundsConfirmationRequest fundsConfirmationRequest = aVrpFundsConfirmationRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    fundsConfirmationRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBVRPFundsConfirmationResponse mockFundsConfirmationResponse = aFundsConfirmationResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockFundsConfirmationResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.headerDoesNotExist("x-idempotency-key"))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBVRPFundsConfirmationResponse fundsConfirmationResponse = restVrpClient.getFundsConfirmation(
            consentId,
            fundsConfirmationRequest,
            accessToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockFundsConfirmationResponse, fundsConfirmationResponse);

        mockAspspServer.verify();
    }

    @Test
    void getFundsConfirmationThrowsVrpApiCallExceptionOnApiCallFailure() {
        String consentId = "vrp-consent-id";
        String accessToken = "access-token";
        OBVRPFundsConfirmationRequest fundsConfirmationRequest = aVrpFundsConfirmationRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    fundsConfirmationRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getFundsConfirmation(
                consentId,
                fundsConfirmationRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails
            ));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialVrpFundsConfirmationResponses.class)
    void getFundsConfirmationThrowsVrpApiCallExceptionPartialResponse(OBVRPFundsConfirmationResponse response) {
        String consentId = "vrp-consent-id";
        String accessToken = "access-token";
        OBVRPFundsConfirmationRequest fundsConfirmationRequest = aVrpFundsConfirmationRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    fundsConfirmationRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getFundsConfirmation(
                consentId,
                fundsConfirmationRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails
            ));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrpConsent() {
        String consentId = "vrp-consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBDomesticVRPConsentResponse mockDomesticVrpConsentResponse = aDomesticVrpConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBDomesticVRPConsentResponse domesticVrpConsentResponse = restVrpClient.getDomesticVrpConsent(
            consentId,
            aspspDetails);

        Assertions.assertEquals(mockDomesticVrpConsentResponse, domesticVrpConsentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrpConsentThrowsVrpApiCallExceptionOnApiCallFailure() {
        String consentId = "vrp-consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticVrpConsentResponses.class)
    void getDomesticVrpConsentThrowsVrpApiCallExceptionPartialResponse(OBDomesticVRPConsentResponse response) {
        String consentId = "vrp-consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }


    @Test
    void deleteDomesticVrpConsent() {
        final String consentId = "vrp-consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBDomesticVRPConsentResponse mockDomesticVrpConsentResponse = aDomesticVrpConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpConsentResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.DELETE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenResponse.getAccessToken()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        restVrpClient.deleteDomesticVrpConsent(
            consentId,
            aspspDetails);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        mockAspspServer.verify();
    }

    @Test
    void deleteDomesticVrpConsentThrowsVrpApiCallExceptionOnApiCallFailure() {
        String consentId = "vrp-consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.DELETE))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.deleteDomesticVrpConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(DeleteVrpConsentResponses.class)
    void deleteDomesticVrpConsentThrowsVrpApiCallExceptionPartialResponse(DefaultResponseCreator response) {
        String consentId = "vrp-consent-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.DELETE))
            .andRespond(response);

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.deleteDomesticVrpConsent(consentId, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticVrp() {
        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();
        String accessToken = "access-token";
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
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header("x-fapi-financial-id", aspspDetails.getOrganisationId()))
            .andExpect(MockRestRequestMatchers.header("x-idempotency-key", IDEMPOTENCY_KEY))
            .andExpect(MockRestRequestMatchers.header("x-jws-signature", DETACHED_JWS_SIGNATURE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().json(jsonConverter.writeValueAsString(domesticVrpRequest)))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        OBDomesticVRPResponse domesticPaymentResponse = restVrpClient.submitDomesticVrp(
            domesticVrpRequest,
            accessToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        mockAspspServer.verify();
    }

    @Test
    void submitDomesticVrpThrowsVrpApiCallExceptionOnApiCallFailure() {
        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        String accessToken = "access-token";

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBDomesticVRPRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest());

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.submitDomesticVrp(
                domesticVrpRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(RestVrpClientTest.PartialDomesticVrpResponses.class)
    void submitDomesticVrpThrowsVrpApiCallExceptionOnPartialResponse(OBDomesticVRPResponse response) {

        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        String accessToken = "access-token";
        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBDomesticVRPRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.submitDomesticVrp(
                domesticVrpRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrp() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

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
    void getDomesticVrpThrowsVrpApiCallExceptionOnApiCallFailure() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrp(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(RestVrpClientTest.PartialDomesticVrpResponses.class)
    void getDomesticVrpThrowsVrpApiCallExceptionPartialResponse(OBDomesticVRPResponse response) {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrp(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void getDomesticVrpDetails() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

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
    void getDomesticVrpDetailsThrowsVrpApiCallExceptionOnApiCallFailure() {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(RestVrpClientTest.PartialDomesticVrpDetailsResponses.class)
    void getDomesticVrpDetailsThrowsVrpApiCallExceptionPartialResponse(OBDomesticVRPDetails response) {
        String vrpId = "vrp-id";
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails));

        mockAspspServer.verify();
    }

    private OBDomesticVRPConsentRequest aDomesticVrpConsentRequest() {
        OBDomesticVRPControlParametersPeriodicLimits periodicLimits = new OBDomesticVRPControlParametersPeriodicLimits()
            .amount("10000.00")
            .currency("GBP")
            .periodAlignment(OBDomesticVRPControlParametersPeriodicLimits.PeriodAlignmentEnum.CALENDAR)
            .periodType(OBDomesticVRPControlParametersPeriodicLimits.PeriodTypeEnum.HALF_YEAR);
        OBDomesticVRPControlParameters controlParameters = new OBDomesticVRPControlParameters()
            .validFromDateTime(OffsetDateTime.now())
            .validToDateTime(OffsetDateTime.now().plusDays(2))
            .maximumIndividualAmount(anActiveOrHistoricCurrencyAndAmount())
            .periodicLimits(List.of(periodicLimits))
            .vrPType(List.of(OBVRPConsentType.SWEEPING, OBVRPConsentType.OTHER))
            .psUAuthenticationMethods(List.of(OBVRPAuthenticationMethods.SCA));
        OBCashAccountDebtorWithName debtorAccount = new OBCashAccountDebtorWithName()
            .schemeName(OBExternalAccountIdentification4Code.SORTCODEACCOUNTNUMBER)
            .identification("756788123456")
            .name("Account Name");
        OBCashAccountCreditor3 creditorAccount = new OBCashAccountCreditor3()
            .schemeName(OBExternalAccountIdentification4Code.SORTCODEACCOUNTNUMBER)
            .identification("231470123456")
            .name("Wise Payments Ltd");
        OBDomesticVRPInitiationRemittanceInformation remittanceInformation = new OBDomesticVRPInitiationRemittanceInformation()
            .reference("reference");
        OBDomesticVRPInitiation initiation = new OBDomesticVRPInitiation()
            .debtorAccount(debtorAccount)
            .creditorAccount(creditorAccount)
            .remittanceInformation(remittanceInformation);
        OBDomesticVRPConsentRequestData data = new OBDomesticVRPConsentRequestData()
            .readRefundAccount(OBDomesticVRPConsentRequestData.ReadRefundAccountEnum.YES)
            .controlParameters(controlParameters)
            .initiation(initiation);
        OBRisk1 risk = new OBRisk1()
            .paymentContextCode(OBRisk1.PaymentContextCodeEnum.OTHER);
        return new OBDomesticVRPConsentRequest()
            .data(data)
            .risk(risk);
    }

    private OBDomesticVRPConsentResponse aDomesticVrpConsentResponse() {
        OBDomesticVRPConsentResponseData data = new OBDomesticVRPConsentResponseData()
            .consentId("vrp-consent-id")
            .status(OBDomesticVRPConsentResponseData.StatusEnum.AUTHORISED);
        return new OBDomesticVRPConsentResponse()
            .data(data);
    }

    private OBActiveOrHistoricCurrencyAndAmount anActiveOrHistoricCurrencyAndAmount() {
        return new OBActiveOrHistoricCurrencyAndAmount()
            .amount("5000.00")
            .currency("GBP");
    }

    private OBVRPFundsConfirmationRequest aVrpFundsConfirmationRequest() {
        OBVRPFundsConfirmationRequestData data = new OBVRPFundsConfirmationRequestData()
            .consentId("vrp-consent-id")
            .instructedAmount(anActiveOrHistoricCurrencyAndAmount())
            .reference("reference");
        return new OBVRPFundsConfirmationRequest()
            .data(data);
    }

    private OBVRPFundsConfirmationResponse aFundsConfirmationResponse() {
        OBPAFundsAvailableResult1 fundsAvailableResult = new OBPAFundsAvailableResult1()
            .fundsAvailable(OBPAFundsAvailableResult1.FundsAvailableEnum.AVAILABLE);
        OBVRPFundsConfirmationResponseData data = new OBVRPFundsConfirmationResponseData()
            .fundsAvailableResult(fundsAvailableResult);
        return new OBVRPFundsConfirmationResponse()
            .data(data);
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

    private static class PartialDomesticVrpConsentResponses implements ArgumentsProvider {


        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData("123", null)),
                Arguments.of(ofData(null, OBDomesticVRPConsentResponseData.StatusEnum.AUTHORISED)),
                Arguments.of(ofData("", OBDomesticVRPConsentResponseData.StatusEnum.AUTHORISED)),
                Arguments.of(ofData(" ", OBDomesticVRPConsentResponseData.StatusEnum.AUTHORISED))
            );
        }

        private OBDomesticVRPConsentResponse nullData() {
            return new OBDomesticVRPConsentResponse();
        }

        private OBDomesticVRPConsentResponse ofData(
            String consentId,
            OBDomesticVRPConsentResponseData.StatusEnum status
        ) {
            OBDomesticVRPConsentResponseData data = new OBDomesticVRPConsentResponseData()
                .consentId(consentId)
                .status(status);
            return new OBDomesticVRPConsentResponse()
                .data(data);
        }
    }

    private static class PartialVrpFundsConfirmationResponses implements ArgumentsProvider {


        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(nullData())
            );
        }

        private OBVRPFundsConfirmationResponse nullData() {
            return new OBVRPFundsConfirmationResponse();
        }
    }

    private static class DeleteVrpConsentResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(MockRestResponseCreators.withServerError()),
                Arguments.of(MockRestResponseCreators.withBadRequest()),
                Arguments.of(MockRestResponseCreators.withUnauthorizedRequest())
            );
        }
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
