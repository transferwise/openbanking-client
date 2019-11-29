package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.aspsp.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

public class RestRegistrationClientTest {

    private MockRestServiceServer mockAspspServer;

    private RestRegistrationClient restRegistrationClient;

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restRegistrationClient = new RestRegistrationClient(restTemplate);
    }

    @Test
    void registerClient() {
        String softwareStatementAssertion = "software-statement-assertion";
        AspspDetails aspspDetails = aAspspDefinition();

        String mockJsonResponse = "json-response";
        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getRegistrationUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, "application/jwt"))
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

    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .registrationUrl("/registration-url")
            .build();
    }
}
