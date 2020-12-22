package com.transferwise.openbanking.client.api.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationRequest;
import com.transferwise.openbanking.client.api.registration.domain.ClientRegistrationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
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
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.UnusedPrivateMethod") // PMD considers argumentsForRegisterClientTest unused
class RestRegistrationClientTest {

    private static ObjectMapper objectMapper;

    @Mock
    private JwtClaimsSigner jwtClaimsSigner;

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

        restRegistrationClient = new RestRegistrationClient(jwtClaimsSigner, restTemplate);
    }

    @ParameterizedTest
    @MethodSource("argumentsForRegisterClientTest")
    void registerClient(boolean registrationUsesJoseContentType, String expectedContentType) throws Exception {
        ClientRegistrationRequest clientRegistrationRequest = aRegistrationClaims();
        AspspDetails aspspDetails = aAspspDefinition(registrationUsesJoseContentType);

        String signedClaims = "signed-claims";
        Mockito.when(jwtClaimsSigner.createSignature(clientRegistrationRequest, aspspDetails))
            .thenReturn(signedClaims);

        ClientRegistrationResponse mockResponse = ClientRegistrationResponse.builder()
            .clientId("client-id")
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

    private static ClientRegistrationRequest aRegistrationClaims() {
        return ClientRegistrationRequest.builder()
            .jti("jwt-id")
            .build();
    }

    private static AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .registrationUrl("/registration-url")
            .build();
    }

    private static AspspDetails aAspspDefinition(boolean registrationUsesJoseContentType) {
        return TestAspspDetails.builder()
            .registrationUrl("/registration-url")
            .registrationUsesJoseContentType(registrationUsesJoseContentType)
            .build();
    }

    private static Stream<Arguments> argumentsForRegisterClientTest() {
        return Stream.of(
            Arguments.of(false, "application/jwt"),
            Arguments.of(true, "application/jose")
        );
    }
}
