package com.transferwise.openbanking.client.api.registration;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.transferwise.openbanking.client.test.factory.AspspDetailsFactory.aTestAspspDetails;
import static com.transferwise.openbanking.client.test.factory.SoftwareStatementDetailsFactory.aSoftwareStatementDetails;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.Scope;
import com.transferwise.openbanking.client.test.factory.AspspDetailsFactory;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({
    "PMD.UnusedPrivateMethod",
    "PMD.AvoidDuplicateLiterals",
    "checkstyle:membername",
    "checkstyle:variabledeclarationusagedistance",
    "checkstyle:methodname"})
class RestRegistrationClientTest {

    private static ObjectMapper objectMapper;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    @Mock
    private OAuthClient oAuthClient;

    private RestRegistrationClient restRegistrationClient;

    private WireMockServer wireMockServer;

    @BeforeAll
    static void initAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        WebClient webClient = WebClient.create("http://localhost:" + wireMockServer.port());

        restRegistrationClient = new RestRegistrationClient(jwtClaimsSigner, oAuthClient, webClient);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @ParameterizedTest
    @MethodSource("argumentsForContentTypeTest")
    void registerClient(boolean registrationUsesJoseContentType, String expectedContentType) throws Exception {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails(registrationUsesJoseContentType);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .clientIdIssuedAt("100")
            .build();
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        WireMock.stubFor(post(urlEqualTo(aspspDetails.getRegistrationUrl()))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(expectedContentType))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT_CHARSET, equalTo(StandardCharsets.UTF_8.name().toLowerCase()))
            .withRequestBody(equalTo(signedClaims))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.registerClient(
            clientRegistrationRequest,
            aspspDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);
        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl())));
    }

    @Test
    void registerClientHandlesTimestampIssuedAtValues() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        String jsonResponse = "{"
            + "\"client_id_issued_at\":\"2021-02-10T12:00:51.191+0000\","
            + "\"client_secret_expires_at\":\"2022-02-10T12:00:51.191+0000\""
            + "}";
        WireMock.stubFor(post(urlEqualTo(aspspDetails.getRegistrationUrl()))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.registerClient(
            clientRegistrationRequest,
            aspspDetails);

        Assertions.assertEquals("2021-02-10T12:00:51.191+0000", registrationResponse.getClientIdIssuedAt());
        Assertions.assertEquals("2022-02-10T12:00:51.191+0000",
            registrationResponse.getClientSecretExpiresAt());

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl())));
    }

    @Test
    void registerClientThrowsApiCallExceptionOnApiCallFailure() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        WireMock.stubFor(post(urlEqualTo(aspspDetails.getRegistrationUrl())).willReturn(serverError()));

        Assertions.assertThrows(ApiCallException.class,
            () -> restRegistrationClient.registerClient(clientRegistrationRequest, aspspDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl())));
    }

    @Test
    void updateRegistration() throws Exception {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(mockAccessTokenResponse);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .build();
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        WireMock.stubFor(put(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/jwt"))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT_CHARSET, equalTo(StandardCharsets.UTF_8.name().toLowerCase()))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer access-token"))
            .withRequestBody(equalTo(signedClaims))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);

        WireMock.verify(exactly(1), putRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id")));
    }

    @ParameterizedTest
    @MethodSource("argumentsForContentTypeTest")
    void updateRegistrationSupportsDifferentContentTypes(boolean registrationUsesJoseContentType,
        String expectedContentType)
        throws Exception {

        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails(registrationUsesJoseContentType);
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(mockAccessTokenResponse);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .build();
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        WireMock.stubFor(put(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(expectedContentType))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);

        WireMock.verify(exactly(1), putRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id")));
    }

    @ParameterizedTest
    @MethodSource("argumentsForAuthenticationScopeTest")
    void updateRegistrationSupportsDifferentAuthenticationScopes(Set<Scope> registrationAuthenticationScopes,
        String expectedAuthenticationScope)
        throws Exception {

        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aTestAspspDetails(false, registrationAuthenticationScopes);
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && Objects.equals(expectedAuthenticationScope, request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(mockAccessTokenResponse);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .build();
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        WireMock.stubFor(put(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);

        WireMock.verify(exactly(1), putRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id")));
    }

    @Test
    void updateRegistrationThrowsApiCallExceptionOnApiCallFailure() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(mockAccessTokenResponse);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        WireMock.stubFor(put(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id")).willReturn(serverError()));

        Assertions.assertThrows(ApiCallException.class,
            () -> restRegistrationClient.updateRegistration(
                clientRegistrationRequest,
                aspspDetails,
                softwareStatementDetails));

        WireMock.verify(exactly(1), putRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id")));
    }

    @ParameterizedTest
    @MethodSource("argumentsForAuthenticationScopeTest")
    void deleteRegistrationSupportsDifferentAuthenticationScopes(Set<Scope> registrationAuthenticationScopes,
        String expectedAuthenticationScope) {

        AspspDetails aspspDetails = aTestAspspDetails(false, registrationAuthenticationScopes);
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type"))
                        && Objects.equals(expectedAuthenticationScope, request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(mockAccessTokenResponse);

        WireMock.stubFor(delete(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer access-token"))
            .willReturn(status(200)));

        restRegistrationClient.deleteRegistration(aspspDetails, softwareStatementDetails);

        WireMock.verify(exactly(1), deleteRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id")));
    }

    @Test
    void deleteRegistrationThrowsApiCallExceptionOnApiCallFailure() {
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(mockAccessTokenResponse);

        WireMock.stubFor(delete(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .willReturn(serverError()));

        Assertions.assertThrows(ApiCallException.class,
            () -> restRegistrationClient.deleteRegistration(aspspDetails, softwareStatementDetails));

        WireMock.verify(exactly(1), deleteRequestedFor(urlEqualTo(aspspDetails.getRegistrationUrl() + "/client-id")));
    }

    private static ClientRegistrationRequest aRegistrationClaims() {
        return ClientRegistrationRequest.builder()
            .jti("jwt-id")
            .build();
    }

    private static Stream<Arguments> argumentsForContentTypeTest() {
        return Stream.of(
            Arguments.of(false, "application/jwt"),
            Arguments.of(true, "application/jose")
        );
    }

    private static Stream<Arguments> argumentsForAuthenticationScopeTest() {
        return Stream.of(
            Arguments.of(Set.of(Scope.PAYMENTS), "payments"),
            Arguments.of(
                new LinkedHashSet<>(List.of(Scope.OPENID, Scope.PAYMENTS)),
                "openid payments"),
            Arguments.of(Set.of(), null)
        );
    }
}
