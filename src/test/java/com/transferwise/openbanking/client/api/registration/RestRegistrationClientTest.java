package com.transferwise.openbanking.client.api.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.api.registration.domain.RegistrationPermission;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.test.TestAspspDetails;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"PMD.UnusedPrivateMethod", "PMD.AvoidDuplicateLiterals"}) // PMD considers argumentsForRegisterClientTest unused
class RestRegistrationClientTest {

    private static ObjectMapper objectMapper;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    @Mock
    private OAuthClient oAuthClient;

    private MockRestServiceServer mockAspspServer;

    private RestRegistrationClient restRegistrationClient;

    @BeforeAll
    static void initAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restRegistrationClient = new RestRegistrationClient(jwtClaimsSigner, oAuthClient, restTemplate);
    }

    @ParameterizedTest
    @MethodSource("argumentsForContentTypeTest")
    void registerClient(boolean registrationUsesJoseContentType, String expectedContentType) throws Exception {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition(registrationUsesJoseContentType);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .clientIdIssuedAt("100")
            .build();
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, expectedContentType))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT_CHARSET,
                StandardCharsets.UTF_8.name().toLowerCase()))
            .andExpect(MockRestRequestMatchers.content().string(signedClaims))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.registerClient(
            clientRegistrationRequest,
            aspspDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);

        mockAspspServer.verify();
    }

    @Test
    void registerClientHandlesTimestampIssuedAtValues() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        String jsonResponse = "{" +
            "\"client_id_issued_at\":\"2021-02-10T12:00:51.191+0000\"," +
            "\"client_secret_expires_at\":\"2022-02-10T12:00:51.191+0000\"" +
            "}";
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.registerClient(
            clientRegistrationRequest,
            aspspDetails);

        Assertions.assertEquals("2021-02-10T12:00:51.191+0000", registrationResponse.getClientIdIssuedAt());
        Assertions.assertEquals("2022-02-10T12:00:51.191+0000",
            registrationResponse.getClientSecretExpiresAt());

        mockAspspServer.verify();
    }

    @Test
    void registerClientThrowsApiCallExceptionOnApiCallFailure() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restRegistrationClient.registerClient(clientRegistrationRequest, aspspDetails));

        mockAspspServer.verify();
    }

    @Test
    void updateRegistration() throws Exception {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        "payments".equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(mockAccessTokenResponse);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .build();
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, "application/jwt"))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT_CHARSET,
                StandardCharsets.UTF_8.name().toLowerCase()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
            .andExpect(MockRestRequestMatchers.content().string(signedClaims))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @MethodSource("argumentsForContentTypeTest")
    void updateRegistrationSupportsDifferentContentTypes(boolean registrationUsesJoseContentType,
                                                         String expectedContentType)
        throws Exception {

        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition(registrationUsesJoseContentType);
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
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, expectedContentType))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @MethodSource("argumentsForAuthenticationScopeTest")
    void updateRegistrationSupportsDifferentAuthenticationScopes(boolean registrationAuthenticationRequiresOpenIdScope,
                                                                 List<RegistrationPermission> tppPermissions,
                                                                 String expectedAuthenticationScope)
        throws Exception {

        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition(false, registrationAuthenticationRequiresOpenIdScope);
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails(tppPermissions);

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito
            .when(oAuthClient.getAccessToken(
                Mockito.argThat(request ->
                    "client_credentials".equals(request.getRequestBody().get("grant_type")) &&
                        expectedAuthenticationScope.equals(request.getRequestBody().get("scope"))),
                Mockito.eq(aspspDetails)))
            .thenReturn(mockAccessTokenResponse);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .build();
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ClientRegistrationResponse registrationResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertEquals(mockResponse, registrationResponse);

        mockAspspServer.verify();
    }

    @Test
    void updateRegistrationThrowsApiCallExceptionOnApiCallFailure() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        AccessTokenResponse mockAccessTokenResponse = AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
        Mockito.when(oAuthClient.getAccessToken(Mockito.any(), Mockito.any()))
            .thenReturn(mockAccessTokenResponse);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restRegistrationClient.updateRegistration(
                clientRegistrationRequest,
                aspspDetails,
                softwareStatementDetails));

        mockAspspServer.verify();
    }

    private static ClientRegistrationRequest aRegistrationClaims() {
        return ClientRegistrationRequest.builder()
            .jti("jwt-id")
            .build();
    }

    private static AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .registrationUrl("/registration-url")
            .clientId("client-id")
            .build();
    }

    private static AspspDetails aAspspDefinition(boolean registrationUsesJoseContentType) {
        return TestAspspDetails.builder()
            .registrationUrl("/registration-url")
            .registrationUsesJoseContentType(registrationUsesJoseContentType)
            .clientId("client-id")
            .build();
    }

    private static AspspDetails aAspspDefinition(boolean registrationUsesJoseContentType,
                                                 boolean registrationAuthenticationRequiresOpenIdScope) {
        return TestAspspDetails.builder()
            .registrationUrl("/registration-url")
            .registrationUsesJoseContentType(registrationUsesJoseContentType)
            .registrationAuthenticationRequiresOpenIdScope(registrationAuthenticationRequiresOpenIdScope)
            .clientId("client-id")
            .build();
    }

    private static SoftwareStatementDetails aSoftwareStatementDetails() {
        return SoftwareStatementDetails.builder()
            .permissions(List.of(RegistrationPermission.PAYMENTS))
            .build();
    }

    private static SoftwareStatementDetails aSoftwareStatementDetails(List<RegistrationPermission> permissions) {
        return SoftwareStatementDetails.builder()
            .permissions(permissions)
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
            Arguments.of(false, List.of(RegistrationPermission.PAYMENTS), "payments"),
            Arguments.of(false, List.of(RegistrationPermission.OPENID, RegistrationPermission.PAYMENTS), "payments"),
            Arguments.of(true, List.of(RegistrationPermission.PAYMENTS), "payments openid"),
            Arguments.of(true,
                List.of(RegistrationPermission.OPENID, RegistrationPermission.PAYMENTS),
                "openid payments")
        );
    }
}
