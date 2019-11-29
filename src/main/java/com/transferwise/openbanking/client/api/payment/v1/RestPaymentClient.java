package com.transferwise.openbanking.client.api.payment.v1;

import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;
import com.transferwise.openbanking.client.aspsp.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import com.transferwise.openbanking.client.oauth.domain.AccessTokenResponse;
import com.transferwise.openbanking.client.oauth.domain.GetAccessTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class RestPaymentClient implements PaymentClient {

    private static final String PAYMENTS_SCOPE = "payments";

    private static final String ENDPOINT_PATH_FORMAT = "%s/open-banking/v1.%s/%s";

    private static final String PAYMENT_RESOURCE = "payments";
    private static final String PAYMENT_SUBMISSION_RESOURCE = "payment-submissions";

    private final OAuthClient oAuthClient;
    private final IdempotencyKeyGenerator idempotencyKeyGenerator;
    private final RestTemplate restTemplate;

    @Override
    public PaymentSetupResponse setupPayment(SetupPaymentRequest setupPaymentRequest, AspspDetails aspspDetails) {

        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest(PAYMENTS_SCOPE);
        AccessTokenResponse accessTokenResponse = oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            accessTokenResponse.getAccessToken(),
            idempotencyKeyGenerator.generateIdempotencyKey(setupPaymentRequest));

        HttpEntity<SetupPaymentRequest> request = new HttpEntity<>(setupPaymentRequest, headers);

        log.info("Calling setup payment API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<PaymentSetupResponse> response = restTemplate.exchange(
                generateApiUrl(aspspDetails, PAYMENT_RESOURCE),
                HttpMethod.POST,
                request,
                PaymentSetupResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to setup payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to setup payment endpoint failed, and no response body returned", e);
        }
    }

    @Override
    public PaymentSubmissionResponse submitPayment(SubmitPaymentRequest submitPaymentRequest,
                                                   String authorizationCode,
                                                   AspspDetails aspspDetails) {

        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.authorizationCodeRequest(authorizationCode,
            aspspDetails.getTppRedirectUrl());
        AccessTokenResponse accessTokenResponse = oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            accessTokenResponse.getAccessToken(),
            idempotencyKeyGenerator.generateIdempotencyKey(submitPaymentRequest));

        HttpEntity<SubmitPaymentRequest> request = new HttpEntity<>(submitPaymentRequest, headers);

        log.info("Calling submit payment API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<PaymentSubmissionResponse> response = restTemplate.exchange(
                generateApiUrl(aspspDetails, PAYMENT_SUBMISSION_RESOURCE),
                HttpMethod.POST,
                request,
                PaymentSubmissionResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, and no response body returned", e);
        }
    }

    @Override
    public PaymentSubmissionResponse getPaymentSubmission(String paymentSubmissionId, AspspDetails aspspDetails) {

        GetAccessTokenRequest getAccessTokenRequest = GetAccessTokenRequest.clientCredentialsRequest(PAYMENTS_SCOPE);
        AccessTokenResponse accessTokenResponse = oAuthClient.getAccessToken(getAccessTokenRequest, aspspDetails);

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getFinancialId(),
            accessTokenResponse.getAccessToken());

        HttpEntity request = new HttpEntity(headers);

        log.info("Calling get submission API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<PaymentSubmissionResponse> response = restTemplate.exchange(
                generateApiUrl(aspspDetails, PAYMENT_SUBMISSION_RESOURCE) + "/{paymentSubmissionId}",
                HttpMethod.GET,
                request,
                PaymentSubmissionResponse.class,
                paymentSubmissionId);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get payment submission endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get payment submission endpoint failed, and no response body returned", e);
        }
    }

    private String generateApiUrl(AspspDetails aspspDetails, String resource) {
        return String.format(ENDPOINT_PATH_FORMAT,
            aspspDetails.getApiBaseUrl("1", resource),
            aspspDetails.getPaymentApiMinorVersion(),
            resource);
    }
}
