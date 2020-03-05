package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@SuppressWarnings("PMD.UnusedPrivateMethod") // PMD considers argumentsForRegisterClientTest unused
public class RestRegistrationClientTest {

    private MockRestServiceServer mockAspspServer;

    private RestRegistrationClient restRegistrationClient;

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restRegistrationClient = new RestRegistrationClient(restTemplate);
    }

    @ParameterizedTest
    @MethodSource("argumentsForRegisterClientTest")
    void registerClient(boolean registrationUsesJoseContentType, String expectedContentType) {
        String softwareStatementAssertion = "software-statement-assertion";
        AspspDetails aspspDetails = aAspspDefinition(registrationUsesJoseContentType);

        String mockJsonResponse = "json-response";
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, expectedContentType))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.name().toLowerCase()))
            .andRespond(MockRestResponseCreators.withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        String registrationResponse = restRegistrationClient.registerClient(softwareStatementAssertion,
            aspspDetails);

        Assertions.assertEquals(mockJsonResponse, registrationResponse);

        mockAspspServer.verify();
    }

    @Test
    void registerClientThrowsApiCallExceptionOnApiCallFailure() {
        String softwareStatementAssertion = "software-statement-assertion";
        AspspDetails aspspDetails = aAspspDefinition();

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restRegistrationClient.registerClient(softwareStatementAssertion, aspspDetails));

        mockAspspServer.verify();
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
