package com.transferwise.openbanking.client.oauth;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.factory.AspspDetailsFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
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
class WebOAuthClientTest {

    private static ObjectMapper objectMapper;

    @Mock
    private ClientAuthentication clientAuthentication;

    private WebOAuthClient webOAuthClient;

    private WireMockServer wireMockServer;

    @BeforeAll
    static void setupAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        WebClient webClient = WebClient.create("http://localhost:" + wireMockServer.port());

        webOAuthClient = new WebOAuthClient(clientAuthentication, webClient);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getAccessToken() throws Exception {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        var expectedBody = getAccessTokenRequest.getRequestBody().entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .reduce((s1, s2) -> s1 + "&" + s2).orElse("");

        AccessTokenResponse mockAccessTokenResponse = aAccessTokenResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockAccessTokenResponse);

        WireMock.stubFor(post(urlEqualTo(aspspDetails.getTokenUrl()))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader("x-fapi-interaction-id", matching(".+"))
            .withRequestBody(containing("grant_type=%s".formatted(getAccessTokenRequest.getGrantType())))
            .withRequestBody(equalTo(expectedBody))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        AccessTokenResponse accessTokenResponse = webOAuthClient.getAccessToken(getAccessTokenRequest,
            aspspDetails);

        Assertions.assertEquals(mockAccessTokenResponse, accessTokenResponse);

        Mockito.verify(clientAuthentication).addClientAuthentication(getAccessTokenRequest, aspspDetails);

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(aspspDetails.getTokenUrl())));
    }

    @Test
    void getAccessTokenThrowsApiCallExceptionOnApiCallFailure() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        WireMock.stubFor(post(urlEqualTo(aspspDetails.getTokenUrl())).willReturn(serverError()));

        Assertions.assertThrows(ApiCallException.class,
            () -> webOAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(aspspDetails.getTokenUrl())));
    }

    @ParameterizedTest
    @ArgumentsSource(PartialAccessTokenResponses.class)
    void getAccessTokenThrowsApiCallExceptionOnApiCallFailureOnPartialResponse(AccessTokenResponse response)
        throws Exception {

        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        String jsonResponse = objectMapper.writeValueAsString(response);

        WireMock.stubFor(post(urlEqualTo(aspspDetails.getTokenUrl()))
            .willReturn(okForContentType(APPLICATION_JSON_VALUE, jsonResponse)));

        Assertions.assertThrows(ApiCallException.class,
            () -> webOAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails));

        WireMock.verify(exactly(1), postRequestedFor(urlEqualTo(aspspDetails.getTokenUrl())));
    }

    private static class PartialAccessTokenResponses implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(nullData()),
                Arguments.of(ofData(null)),
                Arguments.of(ofData("")),
                Arguments.of(ofData(" "))
            );
        }

        private AccessTokenResponse nullData() {
            return new AccessTokenResponse();
        }

        private AccessTokenResponse ofData(String accessToken) {
            return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
        }
    }
}
