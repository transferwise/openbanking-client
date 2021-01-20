package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.payment.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentConsentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentRequest;
import com.transferwise.openbanking.client.api.payment.v3.domain.DomesticPaymentResponse;
import com.transferwise.openbanking.client.api.payment.v3.domain.FundsConfirmationResponse;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
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
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestPaymentClient(RestOperations restOperations,
                             OAuthClient oAuthClient,
                             IdempotencyKeyGenerator<DomesticPaymentConsentRequest, DomesticPaymentRequest> idempotencyKeyGenerator,
                             JwtClaimsSigner jwtClaimsSigner) {
        super(restOperations, oAuthClient);
        this.idempotencyKeyGenerator = idempotencyKeyGenerator;
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    @Override
    public DomesticPaymentConsentResponse createDomesticPaymentConsent(
        DomesticPaymentConsentRequest domesticPaymentConsentRequest,
        AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            getClientCredentialsToken(aspspDetails),
            idempotencyKeyGenerator.generateKeyForSetup(domesticPaymentConsentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentConsentRequest, aspspDetails));

        HttpEntity<DomesticPaymentConsentRequest> request = new HttpEntity<>(domesticPaymentConsentRequest, headers);

        log.info("Calling create payment consent API, with interaction ID {}", headers.getInteractionId());

        DomesticPaymentConsentResponse domesticPaymentConsentResponse;
        try {
            ResponseEntity<DomesticPaymentConsentResponse> response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_CONSENT_RESOURCE),
                HttpMethod.POST,
                request,
                DomesticPaymentConsentResponse.class);
            domesticPaymentConsentResponse = response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to create payment consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to create payment consent endpoint failed, and no response body returned", e);
        }

        validateResponse(domesticPaymentConsentResponse);

        return domesticPaymentConsentResponse;
    }

    @Override
    public DomesticPaymentResponse submitDomesticPayment(DomesticPaymentRequest domesticPaymentRequest,
                                                         AuthorizationContext authorizationContext,
                                                         AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getFinancialId(),
            exchangeAuthorizationCode(authorizationContext, aspspDetails),
            idempotencyKeyGenerator.generateKeyForSubmission(domesticPaymentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentRequest, aspspDetails));

        HttpEntity<DomesticPaymentRequest> request = new HttpEntity<>(domesticPaymentRequest, headers);

        log.info("Calling submit payment API, with interaction ID {}", headers.getInteractionId());

        DomesticPaymentResponse domesticPaymentResponse;
        try {
            ResponseEntity<DomesticPaymentResponse> response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_RESOURCE),
                HttpMethod.POST,
                request,
                DomesticPaymentResponse.class);
            domesticPaymentResponse = response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, and no response body returned", e);
        }

        validateResponse(domesticPaymentResponse);

        return domesticPaymentResponse;
    }

    @Override
    public DomesticPaymentConsentResponse getDomesticPaymentConsent(String consentId, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getFinancialId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment consent API, with interaction ID {}", headers.getInteractionId());

        DomesticPaymentConsentResponse domesticPaymentConsentResponse;
        try {
            ResponseEntity<DomesticPaymentConsentResponse> response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_CONSENT_RESOURCE) + "/{consentId}",
                HttpMethod.GET,
                request,
                DomesticPaymentConsentResponse.class,
                consentId);
            domesticPaymentConsentResponse = response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get payment consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get payment consent endpoint failed, and no response body returned", e);
        }

        validateResponse(domesticPaymentConsentResponse);

        return domesticPaymentConsentResponse;
    }

    @Override
    public DomesticPaymentResponse getDomesticPayment(String domesticPaymentId, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getFinancialId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment API, with interaction ID {}", headers.getInteractionId());

        DomesticPaymentResponse domesticPaymentResponse;
        try {
            ResponseEntity<DomesticPaymentResponse> response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_RESOURCE) + "/{domesticPaymentId}",
                HttpMethod.GET,
                request,
                DomesticPaymentResponse.class,
                domesticPaymentId);
            domesticPaymentResponse = response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get payment endpoint failed, and no response body returned", e);
        }

        validateResponse(domesticPaymentResponse);

        return domesticPaymentResponse;
    }

    @Override
    public FundsConfirmationResponse getFundsConfirmation(String consentId,
                                                          AuthorizationContext authorizationContext,
                                                          AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getFinancialId(),
            exchangeAuthorizationCode(authorizationContext, aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get confirmation of funds API, with interaction ID {}", headers.getInteractionId());

        FundsConfirmationResponse fundsConfirmationResponse;
        try {
            ResponseEntity<FundsConfirmationResponse> response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_CONSENT_RESOURCE) + "/{consentId}/funds-confirmation",
                HttpMethod.GET,
                request,
                FundsConfirmationResponse.class,
                consentId);
            fundsConfirmationResponse = response.getBody();
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get confirmation of funds endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get confirmation of funds endpoint failed, and no response body returned",
                e);
        }

        validateResponse(fundsConfirmationResponse);

        return fundsConfirmationResponse;
    }

    private String generateApiUrl(AspspDetails aspspDetails, String resource) {
        return String.format(ENDPOINT_PATH_FORMAT,
            aspspDetails.getApiBaseUrl("3", resource),
            aspspDetails.getPaymentApiMinorVersion(),
            resource);
    }

    private void validateResponse(DomesticPaymentConsentResponse response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getConsentId() == null ||
            response.getData().getConsentId().isBlank()) {
            throw new ApiCallException("Empty or partial domestic payment consent response returned " + response);
        }
    }

    private void validateResponse(DomesticPaymentResponse response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getDomesticPaymentId() == null ||
            response.getData().getDomesticPaymentId().isBlank()) {
            throw new ApiCallException("Empty or partial domestic payment response returned " + response);
        }
    }

    private void validateResponse(FundsConfirmationResponse response) {
        if (response == null || response.getData() == null) {
            throw new ApiCallException("Empty or partial funds confirmation response returned " + response);
        }
    }
}
