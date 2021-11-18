package com.transferwise.openbanking.client.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
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
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

import static com.transferwise.openbanking.client.test.factory.AccessTokenResponseFactory.aAccessTokenResponse;

@ExtendWith(MockitoExtension.class)
class RestOAuthClientTest {

    private static ObjectMapper objectMapper;

    @Mock
    private ClientAuthentication clientAuthentication;

    private MockRestServiceServer mockAspspServer;

    private RestOAuthClient restOAuthClient;

    @BeforeAll
    static void setupAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restOAuthClient = new RestOAuthClient(clientAuthentication, restTemplate);
    }

    @Test
    void getAccessToken() throws Exception {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        MultiValueMap<String, String> expectedBody = new LinkedMultiValueMap<>();
        getAccessTokenRequest.getRequestBody().forEach(expectedBody::add);

        AccessTokenResponse mockAccessTokenResponse = aAccessTokenResponse();
        String jsonResponse = objectMapper.writeValueAsString(mockAccessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getTokenUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().formData(expectedBody))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        AccessTokenResponse accessTokenResponse = restOAuthClient.getAccessToken(getAccessTokenRequest,
            aspspDetails);

        Assertions.assertEquals(mockAccessTokenResponse, accessTokenResponse);

        Mockito.verify(clientAuthentication).addClientAuthentication(getAccessTokenRequest, aspspDetails);

        mockAspspServer.verify();
    }

    @Test
    void getAccessTokenThrowsApiCallExceptionOnApiCallFailure() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getTokenUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restOAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails));

        mockAspspServer.verify();
    }

    @ParameterizedTest
    @ArgumentsSource(PartialAccessTokenResponses.class)
    void getAccessTokenThrowsApiCallExceptionOnApiCallFailureOnPartialResponse(AccessTokenResponse response)
        throws Exception {

        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = AspspDetailsFactory.aTestAspspDetails();

        String jsonResponse = objectMapper.writeValueAsString(response);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getTokenUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Assertions.assertThrows(ApiCallException.class,
            () -> restOAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails));

        mockAspspServer.verify();
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
