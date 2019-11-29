package com.transferwise.openbanking.client.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transferwise.openbanking.client.aspsp.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import com.transferwise.openbanking.client.test.TestAspspDetails;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        AspspDetails aspspDetails = aAspspDefinition();

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
        AspspDetails aspspDetails = aAspspDefinition();

        mockAspspServer.expect(MockRestRequestMatchers.requestTo(aspspDetails.getTokenUrl()))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        Assertions.assertThrows(ApiCallException.class,
            () -> restOAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails));

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
}
