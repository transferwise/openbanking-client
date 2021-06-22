package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.json.JacksonJsonConverter;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.ErrorResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.TestAspspDetails;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class RestOAuthClientTest {

    private static JsonConverter jsonConverter;

    @Mock
    private ClientAuthentication clientAuthentication;

    private MockRestServiceServer mockAspspServer;

    private RestOAuthClient restOAuthClient;

    @BeforeAll
    static void setupAll() {
        jsonConverter = new JacksonJsonConverter();
    }

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockAspspServer = MockRestServiceServer.createServer(restTemplate);

        restOAuthClient = new RestOAuthClient(restTemplate, jsonConverter, clientAuthentication);
    }

    @Test
    void getAccessTokenReturnsSuccessResponseOnApiCallSuccess() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = aAspspDefinition();

        MultiValueMap<String, String> expectedBody = new LinkedMultiValueMap<>();
        getAccessTokenRequest.getRequestBody().forEach(expectedBody::add);

        AccessTokenResponse mockAccessTokenResponse = aAccessTokenResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockAccessTokenResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getTokenUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.header("x-fapi-interaction-id", CoreMatchers.notNullValue()))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andExpect(MockRestRequestMatchers.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockRestRequestMatchers.content().formData(expectedBody))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<AccessTokenResponse, ErrorResponse> apiResponse = restOAuthClient.getAccessToken(
            getAccessTokenRequest,
            aspspDetails);

        Assertions.assertFalse(apiResponse.isCallFailed());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertEquals(mockAccessTokenResponse, apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertNull(apiResponse.getFailureException());

        Mockito.verify(clientAuthentication).addClientAuthentication(getAccessTokenRequest, aspspDetails);

        mockAspspServer.verify();
    }

    @Test
    void getAccessTokenReturnsFailureResponseOnApiCallFailure() {
        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = aAspspDefinition();

        ErrorResponse mockErrorResponse = aErrorResponse();
        String jsonResponse = jsonConverter.writeValueAsString(mockErrorResponse);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getTokenUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError().body(jsonResponse));

        ApiResponse<AccessTokenResponse, ErrorResponse> apiResponse = restOAuthClient.getAccessToken(
            getAccessTokenRequest,
            aspspDetails);

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

    @ParameterizedTest
    @ArgumentsSource(PartialAccessTokenResponses.class)
    void getAccessTokenReturnsFailureResponseOnPartialResponse(AccessTokenResponse response) {

        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest("payments");
        AspspDetails aspspDetails = aAspspDefinition();

        String jsonResponse = jsonConverter.writeValueAsString(response);

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getTokenUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ApiResponse<AccessTokenResponse, ErrorResponse> apiResponse = restOAuthClient.getAccessToken(
            getAccessTokenRequest,
            aspspDetails);

        Assertions.assertTrue(apiResponse.isCallFailed());
        Assertions.assertEquals(200, apiResponse.getStatusCode());
        Assertions.assertEquals(jsonResponse, apiResponse.getResponseBody());
        Assertions.assertNull(apiResponse.getSuccessResponseBody());
        Assertions.assertNull(apiResponse.getFailureResponseBody());
        Assertions.assertTrue(apiResponse.getFailureException() instanceof ApiCallException);

        mockAspspServer.verify();
    }


    private AspspDetails aAspspDefinition() {
        return TestAspspDetails.builder()
            .tokenUrl("/token-url")
            .build();
    }

    private AccessTokenResponse aAccessTokenResponse() {
        return AccessTokenResponse.builder()
            .accessToken("access-token")
            .build();
    }

    private ErrorResponse aErrorResponse() {
        return ErrorResponse.builder()
            .error("Service not found")
            .build();
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
