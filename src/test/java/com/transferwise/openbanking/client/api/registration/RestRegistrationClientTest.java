package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.api.registration.domain.ErrorResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JacksonJsonConverter;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
import com.transferwise.openbanking.client.oauth.domain.Scope;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"PMD.UnusedPrivateMethod", "PMD.AvoidDuplicateLiterals"}) // PMD considers argumentsForRegisterClientTest unused
class RestRegistrationClientTest {

    private static JsonConverter jsonConverter;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

    private MockRestServiceServer mockAspspServer;

    private RestRegistrationClient restRegistrationClient;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restRegistrationClient = new RestRegistrationClient(restTemplate, jsonConverter, jwtClaimsSigner);
    }

    @ParameterizedTest
    @MethodSource("argumentsForContentTypeTest")
    void registerClientReturnsSuccessResponseOnApiCallSuccess(boolean registrationUsesJoseContentType,
                                                              String expectedContentType) {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition(registrationUsesJoseContentType);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .clientIdIssuedAt("100")
            .build();
        String jsonResponse = jsonConverter.writeValueAsString(mockResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, expectedContentType))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT_CHARSET,
                StandardCharsets.UTF_8.name().toLowerCase()))
            .andExpect(MockRestRequestMatchers.content().string(signedClaims))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<ClientRegistrationResponse, ErrorResponse> apiResponse = restRegistrationClient.registerClient(
            clientRegistrationRequest,
            aspspDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

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

        ApiResponse<ClientRegistrationResponse, ErrorResponse> apiResponse = restRegistrationClient.registerClient(
            clientRegistrationRequest,
            aspspDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals("2021-02-10T12:00:51.191+0000",
            apiResponse.getSuccessResponseBody().getClientIdIssuedAt());
        Assertions.assertEquals("2022-02-10T12:00:51.191+0000",
            apiResponse.getSuccessResponseBody().getClientSecretExpiresAt());

        mockAspspServer.verify();
    }

    @Test
    void registerClientReturnsFailureResponseOnApiCallFailure() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError().body("internal server error"));

        ApiResponse<ClientRegistrationResponse, ErrorResponse> apiResponse = restRegistrationClient.registerClient(
            clientRegistrationRequest,
            aspspDetails);

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

    @Test
    void updateRegistrationReturnsSuccessResponseOnApiCallSuccess() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .build();
        String jsonResponse = jsonConverter.writeValueAsString(mockResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, "application/jwt"))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT_CHARSET,
                StandardCharsets.UTF_8.name().toLowerCase()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, "Bearer " + clientCredentialsToken))
            .andExpect(MockRestRequestMatchers.content().string(signedClaims))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<ClientRegistrationResponse, ErrorResponse> apiResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            clientCredentialsToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @MethodSource("argumentsForContentTypeTest")
    void updateRegistrationSupportsDifferentContentTypes(boolean registrationUsesJoseContentType,
                                                         String expectedContentType) {

        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition(registrationUsesJoseContentType);
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
            .build();
        String jsonResponse = jsonConverter.writeValueAsString(mockResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, expectedContentType))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<ClientRegistrationResponse, ErrorResponse> apiResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            clientCredentialsToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        mockAspspServer.verify();
    }

    @Test
    void updateRegistrationReturnsFailureResponseOnApiCallFailure() {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        String clientCredentialsToken = "client-credentials-token";
        AspspDetails aspspDetails = aAspspDefinition();
        SoftwareStatementDetails softwareStatementDetails = aSoftwareStatementDetails();

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ErrorResponse mockErrorResponse = aErrorResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockErrorResponse);
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl() + "/client-id"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
            .andRespond(MockRestResponseCreators.withServerError().body(jsonResponse));

        ApiResponse<ClientRegistrationResponse, ErrorResponse> apiResponse = restRegistrationClient.updateRegistration(
            clientRegistrationRequest,
            clientCredentialsToken,
            aspspDetails,
            softwareStatementDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(500, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertEquals(mockErrorResponse, apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof HttpServerErrorException.InternalServerError);
        Assertions.assertFalse(apiResponse.isClientErrorResponse());
        Assertions.assertTrue(apiResponse.isServerErrorResponse());

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
            .registrationAuthenticationScopes(Set.of(Scope.PAYMENTS))
            .clientId("client-id")
            .build();
    }

    private static AspspDetails aAspspDefinition(boolean registrationUsesJoseContentType) {
        return TestAspspDetails.builder()
            .registrationUrl("/registration-url")
            .registrationUsesJoseContentType(registrationUsesJoseContentType)
            .registrationAuthenticationScopes(Set.of(Scope.PAYMENTS))
            .clientId("client-id")
            .build();
    }

    private static SoftwareStatementDetails aSoftwareStatementDetails() {
        return SoftwareStatementDetails.builder()
            .permissions(List.of(Scope.PAYMENTS))
            .build();
    }

    private static ErrorResponse aErrorResponse() {
        return ErrorResponse.builder()
            .error("invalid request")
            .build();
    }

    private static Stream<Arguments> argumentsForContentTypeTest() {
        return Stream.of(
            Arguments.of(false, "application/jwt"),
            Arguments.of(true, "application/jose")
        );
    }
}
