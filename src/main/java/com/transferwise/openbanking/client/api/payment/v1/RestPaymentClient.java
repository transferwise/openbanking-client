package com.transferwise.openbanking.client.api.payment.v1;

import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.TppConfiguration;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.oauth.OAuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

@Slf4j
public class RestPaymentClient extends BasePaymentClient implements PaymentClient {

    private static final String ENDPOINT_PATH_FORMAT = "%s/open-banking/v1.%s/%s";

    private static final String PAYMENT_RESOURCE = "payments";
    private static final String PAYMENT_SUBMISSION_RESOURCE = "payment-submissions";

    private final IdempotencyKeyGenerator<SetupPaymentRequest, SubmitPaymentRequest> idempotencyKeyGenerator;

    public RestPaymentClient(TppConfiguration tppConfiguration,
                             RestOperations restOperations,
                             OAuthClient oAuthClient,
                             IdempotencyKeyGenerator<SetupPaymentRequest, SubmitPaymentRequest> idempotencyKeyGenerator) {
        super(tppConfiguration, restOperations, oAuthClient);
        this.idempotencyKeyGenerator = idempotencyKeyGenerator;
    }

    @Override
    public PaymentSetupResponse setupPayment(SetupPaymentRequest setupPaymentRequest, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            getClientCredentialsToken(aspspDetails),
            idempotencyKeyGenerator.generateKeyForSetup(setupPaymentRequest));

        HttpEntity<SetupPaymentRequest> request = new HttpEntity<>(setupPaymentRequest, headers);

        log.info("Calling setup payment API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<PaymentSetupResponse> response = restOperations.exchange(
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

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            exchangeAuthorizationCode(authorizationCode, aspspDetails),
            idempotencyKeyGenerator.generateKeyForSubmission(submitPaymentRequest));

        HttpEntity<SubmitPaymentRequest> request = new HttpEntity<>(submitPaymentRequest, headers);

        log.info("Calling submit payment API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<PaymentSubmissionResponse> response = restOperations.exchange(
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

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getFinancialId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get submission API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<PaymentSubmissionResponse> response = restOperations.exchange(
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
