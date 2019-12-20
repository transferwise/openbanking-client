package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentResponse;
import com.transferwise.openbanking.client.aspsp.AspspDetails;
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

    private static final String ENDPOINT_PATH_FORMAT = "%s/open-banking/v3.%s/pisp/%s";

    private static final String PAYMENT_CONSENT_RESOURCE = "domestic-payment-consents";
    private static final String PAYMENT_RESOURCE = "domestic-payments";

    private final IdempotencyKeyGenerator<DomesticPaymentConsentRequest, DomesticPaymentRequest> idempotencyKeyGenerator;
    private final RestOperations restTemplate;

    public RestPaymentClient(OAuthClient oAuthClient,
                             IdempotencyKeyGenerator<DomesticPaymentConsentRequest, DomesticPaymentRequest> idempotencyKeyGenerator,
                             RestOperations restTemplate) {
        super(oAuthClient);
        this.idempotencyKeyGenerator = idempotencyKeyGenerator;
        this.restTemplate = restTemplate;
    }

    @Override
    public DomesticPaymentConsentResponse createDomesticPaymentConsent(
        DomesticPaymentConsentRequest domesticPaymentConsentRequest,
        AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            getClientCredentialsToken(aspspDetails),
            idempotencyKeyGenerator.generateKeyForSetup(domesticPaymentConsentRequest));

        HttpEntity<DomesticPaymentConsentRequest> request = new HttpEntity<>(domesticPaymentConsentRequest, headers);

        log.info("Calling create payment consent API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<DomesticPaymentConsentResponse> response = restTemplate.exchange(
                generateApiUrl(aspspDetails, PAYMENT_CONSENT_RESOURCE),
                HttpMethod.POST,
                request,
                DomesticPaymentConsentResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to create payment consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to create payment consent endpoint failed, and no response body returned", e);
        }
    }

    @Override
    public DomesticPaymentResponse submitDomesticPayment(DomesticPaymentRequest domesticPaymentRequest,
                                                         String authorizationCode,
                                                         AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            exchangeAuthorizationCode(authorizationCode, aspspDetails),
            idempotencyKeyGenerator.generateKeyForSubmission(domesticPaymentRequest));

        HttpEntity<DomesticPaymentRequest> request = new HttpEntity<>(domesticPaymentRequest, headers);

        log.info("Calling submit payment API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<DomesticPaymentResponse> response = restTemplate.exchange(
                generateApiUrl(aspspDetails, PAYMENT_RESOURCE),
                HttpMethod.POST,
                request,
                DomesticPaymentResponse.class);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, and no response body returned", e);
        }
    }

    @Override
    public DomesticPaymentResponse getDomesticPayment(String domesticPaymentId, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getFinancialId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment API, with interaction ID {}", headers.getInteractionId());

        try {
            ResponseEntity<DomesticPaymentResponse> response = restTemplate.exchange(
                generateApiUrl(aspspDetails, PAYMENT_RESOURCE) + "/{domesticPaymentId}",
                HttpMethod.GET,
                request,
                DomesticPaymentResponse.class,
                domesticPaymentId);
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get payment endpoint failed, and no response body returned", e);
        }
    }

    private String generateApiUrl(AspspDetails aspspDetails, String resource) {
        return String.format(ENDPOINT_PATH_FORMAT,
            aspspDetails.getApiBaseUrl("3", resource),
            aspspDetails.getPaymentApiMinorVersion(),
            resource);
    }
}
