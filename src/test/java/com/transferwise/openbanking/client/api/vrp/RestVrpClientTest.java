package com.transferwise.openbanking.client.api.vrp;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBActiveOrHistoricCurrencyAndAmount;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBCashAccountCreditor3;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBCashAccountDebtorWithName;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentRequestData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponseData;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponseData.StatusEnum;
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
import java.time.OffsetDateTime;
import java.util.List;
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
@SuppressWarnings({
    "PMD.AvoidDuplicateLiterals",
    "checkstyle:membername",
    "checkstyle:variabledeclarationusagedistance",
    "checkstyle:methodname",
    "checkstyle:abbreviationaswordinname"})
class RestVrpClientTest {

    private static final String IDEMPOTENCY_KEY = "idempotency-key";
    private static final String DETACHED_JWS_SIGNATURE = "detached-jws-signature";
    public static final String DOMESTIC_VRP_CONSENTS_URL = "/open-banking/v3.1/pisp/domestic-vrp-consents";
    public static final String DOMESTIC_VRP_URL = "/open-banking/v3.1/pisp/domestic-vrps";
    private static JsonConverter jsonConverter;
    private static AspspDetails aspspDetails;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private IdempotencyKeyGenerator<OBDomesticVRPConsentRequest, OBDomesticVRPRequest> idempotencyKeyGenerator;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private WireMockServer wireMockServer;

    private RestVrpClient restVrpClient;

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

        restVrpClient = new RestVrpClient(
            webClient,
            jsonConverter,
            oAuthClient,
            idempotencyKeyGenerator,
            jwtClaimsSigner);

    }

    @Test
    void createDomesticVrpConsent() {
        OBDomesticVRPConsentRequest domesticVRPConsentRequest = aDomesticVrpConsentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
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
        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader("x-idempotency-key", equalTo(IDEMPOTENCY_KEY))
            .withHeader("x-jws-signature", equalTo(DETACHED_JWS_SIGNATURE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalTo(jsonConverter.writeValueAsString(domesticVRPConsentRequest)))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBDomesticVRPConsentResponse domesticVrpConsentResponse = restVrpClient.createDomesticVrpConsent(
            domesticVRPConsentRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticVrpConsentResponse, domesticVrpConsentResponse);

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL)));
    }

    @Test
    void createDomesticVrpConsentThrowsVrpApiCallExceptionOnApiCallFailure() {
        OBDomesticVRPConsentRequest domesticVRPConsentRequest = aDomesticVrpConsentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBDomesticVRPConsentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL)).willReturn(serverError()));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.createDomesticVrpConsent(
                domesticVRPConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL)));
    }


    @ParameterizedTest
    @ArgumentsSource(PartialDomesticVrpConsentResponses.class)
    void createDomesticVrpConsentThrowsVrpApiCallExceptionOnPartialResponse(OBDomesticVRPConsentResponse response) {
        OBDomesticVRPConsentRequest domesticVRPConsentRequest = aDomesticVrpConsentRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        Mockito.when(idempotencyKeyGenerator.generateKeyForSetup(Mockito.any(OBDomesticVRPConsentRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL)).willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.createDomesticVrpConsent(
                domesticVRPConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL)));
    }

    @Test
    void getFundsConfirmation() {
        String consentId = "vrp-consent-id";
        String accessToken = "access-token";
        OBVRPFundsConfirmationRequest fundsConfirmationRequest = aVrpFundsConfirmationRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    fundsConfirmationRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        OBVRPFundsConfirmationResponse mockFundsConfirmationResponse = aFundsConfirmationResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockFundsConfirmationResponse);
        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation"))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessToken))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader("x-idempotency-key", absent())
            .withHeader("x-jws-signature", equalTo(DETACHED_JWS_SIGNATURE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBVRPFundsConfirmationResponse fundsConfirmationResponse = restVrpClient.getFundsConfirmation(
            consentId,
            fundsConfirmationRequest,
            accessToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockFundsConfirmationResponse, fundsConfirmationResponse);

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation")));
    }

    @Test
    void getFundsConfirmationThrowsVrpApiCallExceptionOnApiCallFailure() {
        String consentId = "vrp-consent-id";
        String accessToken = "access-token";
        OBVRPFundsConfirmationRequest fundsConfirmationRequest = aVrpFundsConfirmationRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    fundsConfirmationRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation")).willReturn(serverError()));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getFundsConfirmation(
                consentId,
                fundsConfirmationRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails
            ));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation")));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialVrpFundsConfirmationResponses.class)
    void getFundsConfirmationThrowsVrpApiCallExceptionPartialResponse(OBVRPFundsConfirmationResponse response) {
        String consentId = "vrp-consent-id";
        String accessToken = "access-token";
        OBVRPFundsConfirmationRequest fundsConfirmationRequest = aVrpFundsConfirmationRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        Mockito.when(
                jwtClaimsSigner.createDetachedSignature(
                    fundsConfirmationRequest,
                    aspspDetails,
                    softwareStatementDetails))
            .thenReturn(DETACHED_JWS_SIGNATURE);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation"))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getFundsConfirmation(
                consentId,
                fundsConfirmationRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails
            ));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId + "/funds-confirmation")));
    }

    @ParameterizedTest
    @ArgumentsSource(GetDomesticVrpConsentResponseArgumentProvider.class)
    void getDomesticVrpConsent(
        String jsonResponse,
        OBDomesticVRPConsentResponse expectedDomesticVrpConsentResponse) {
        String consentId = "vrp-consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);
        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBDomesticVRPConsentResponse actualDomesticVrpConsentResponse = restVrpClient.getDomesticVrpConsent(
            consentId,
            aspspDetails);

        Assertions.assertEquals(expectedDomesticVrpConsentResponse, actualDomesticVrpConsentResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)));
    }

    @Test
    void getDomesticVrpConsentThrowsVrpApiCallExceptionOnApiCallFailure() {
        String consentId = "vrp-consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .willReturn(serverError()));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpConsent(consentId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialDomesticVrpConsentResponses.class)
    void getDomesticVrpConsentThrowsVrpApiCallExceptionPartialResponse(OBDomesticVRPConsentResponse response) {
        String consentId = "vrp-consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpConsent(consentId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)));
    }


    @Test
    void deleteDomesticVrpConsent() {
        final String consentId = "vrp-consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBDomesticVRPConsentResponse mockDomesticVrpConsentResponse = aDomesticVrpConsentResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpConsentResponse);
        WireMock.stubFor(delete(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        restVrpClient.deleteDomesticVrpConsent(
            consentId,
            aspspDetails);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        WireMock.verify(exactly(1), deleteRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)));
    }

    @Test
    void deleteDomesticVrpConsentThrowsVrpApiCallExceptionOnApiCallFailure() {
        String consentId = "vrp-consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(delete(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)).willReturn(serverError()));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.deleteDomesticVrpConsent(consentId, aspspDetails));

        WireMock.verify(exactly(1), deleteRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)));
    }

    @ParameterizedTest
    @ArgumentsSource(DeleteVrpConsentResponses.class)
    void deleteDomesticVrpConsentThrowsVrpApiCallExceptionPartialResponse(ResponseDefinitionBuilder response) {
        String consentId = "vrp-consent-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(delete(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)).willReturn(response));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.deleteDomesticVrpConsent(consentId, aspspDetails));

        WireMock.verify(exactly(1), deleteRequestedFor(urlEqualTo(DOMESTIC_VRP_CONSENTS_URL + "/" + consentId)));
    }

    @Test
    void submitDomesticVrp() {
        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
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
        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_URL))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessToken))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader("x-idempotency-key", equalTo(IDEMPOTENCY_KEY))
            .withHeader("x-jws-signature", equalTo(DETACHED_JWS_SIGNATURE))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalTo(jsonConverter.writeValueAsString(domesticVrpRequest)))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBDomesticVRPResponse domesticPaymentResponse = restVrpClient.submitDomesticVrp(
            domesticVrpRequest,
            accessToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockDomesticPaymentResponse, domesticPaymentResponse);

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_URL)));
    }

    @Test
    void submitDomesticVrpThrowsVrpApiCallExceptionOnApiCallFailure() {
        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        String accessToken = "access-token";

        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBDomesticVRPRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_URL)).willReturn(badRequest()));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.submitDomesticVrp(
                domesticVrpRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_URL)));
    }

    @ParameterizedTest
    @ArgumentsSource(RestVrpClientTest.PartialDomesticVrpResponses.class)
    void submitDomesticVrpThrowsVrpApiCallExceptionOnPartialResponse(OBDomesticVRPResponse response) {

        OBDomesticVRPRequest domesticVrpRequest = aDomesticVrpRequest();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        String accessToken = "access-token";
        Mockito.when(idempotencyKeyGenerator.generateKeyForSubmission(Mockito.any(OBDomesticVRPRequest.class)))
            .thenReturn(IDEMPOTENCY_KEY);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(post(urlEqualTo(DOMESTIC_VRP_URL)).willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.submitDomesticVrp(
                domesticVrpRequest,
                accessToken,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(DOMESTIC_VRP_URL)));
    }

    @Test
    void getDomesticVrp() {
        String vrpId = "vrp-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBDomesticVRPResponse mockDomesticVrpResponse = aDomesticVrpResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpResponse);
        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBDomesticVRPResponse domesticVrpResponse = restVrpClient.getDomesticVrp(vrpId, aspspDetails);

        Assertions.assertEquals(mockDomesticVrpResponse, domesticVrpResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId)));
    }

    @Test
    void getDomesticVrpThrowsVrpApiCallExceptionOnApiCallFailure() {
        String vrpId = "vrp-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId)).willReturn(serverError()));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrp(vrpId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId)));
    }

    @ParameterizedTest
    @ArgumentsSource(RestVrpClientTest.PartialDomesticVrpResponses.class)
    void getDomesticVrpThrowsVrpApiCallExceptionPartialResponse(OBDomesticVRPResponse response) {
        String vrpId = "vrp-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId)).willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrp(vrpId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId)));
    }

    @Test
    void getDomesticVrpDetails() {
        String vrpId = "vrp-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(accessTokenResponse);

        OBDomesticVRPDetails mockDomesticVrpDetailsResponse = aDomesticVrpDetailsResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockDomesticVrpDetailsResponse);
        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details"))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + accessTokenResponse.getAccessToken()))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withHeader("x-fapi-financial-id", equalTo(aspspDetails.getOrganisationId()))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        OBDomesticVRPDetails domesticVrpDetailsResponse = restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails);

        Assertions.assertEquals(mockDomesticVrpDetailsResponse, domesticVrpDetailsResponse);

        Mockito.verify(jwtClaimsSigner, Mockito.never())
            .createDetachedSignature(Mockito.any(), Mockito.any(), Mockito.any());

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details")));
    }

    @Test
    void getDomesticVrpDetailsThrowsVrpApiCallExceptionOnApiCallFailure() {
        String vrpId = "vrp-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details")).willReturn(serverError()));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details")));
    }

    @ParameterizedTest
    @ArgumentsSource(RestVrpClientTest.PartialDomesticVrpDetailsResponses.class)
    void getDomesticVrpDetailsThrowsVrpApiCallExceptionPartialResponse(OBDomesticVRPDetails response) {
        String vrpId = "vrp-id";

        AccessTokenResponse accessTokenResponse = aAccessTokenResponse();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(accessTokenResponse);

        String jsonResponse = jsonConverter.writeValueAsString(response);
        WireMock.stubFor(get(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details"))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(VrpApiCallException.class,
            () -> restVrpClient.getDomesticVrpDetails(vrpId, aspspDetails));

        WireMock.verify(exactly(1), getRequestedFor(urlEqualTo(DOMESTIC_VRP_URL + "/" + vrpId + "/payment-details")));
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

    private static class GetDomesticVrpConsentResponseArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            var mockDomesticVrpConsentAcceptResponse = getAcceptedResponse();
            String jsonResponse = jsonConverter.writeValueAsString(
                mockDomesticVrpConsentAcceptResponse);
            return Stream.of(
                Arguments.of(jsonResponse, mockDomesticVrpConsentAcceptResponse),
                Arguments.of(
                    jsonResponse.replace(
                        "Authorised",
                        "Rejected"),
                    getRejectedResponse()));
        }

        private OBDomesticVRPConsentResponse getAcceptedResponse() {
            OBDomesticVRPConsentResponseData acceptedData =
                new OBDomesticVRPConsentResponseData()
                    .consentId("vrp-consent-id")
                    .status(StatusEnum.AUTHORISED);
            return new OBDomesticVRPConsentResponse().data(acceptedData);
        }

        private OBDomesticVRPConsentResponse getRejectedResponse() {
            OBDomesticVRPConsentResponseData rejectedData =
                new OBDomesticVRPConsentResponseData()
                    .consentId("vrp-consent-id")
                    .status(StatusEnum.REJECTED);
            return new OBDomesticVRPConsentResponse().data(rejectedData);
        }
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
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(serverError()),
                Arguments.of(badRequest()),
                Arguments.of(unauthorized())
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
