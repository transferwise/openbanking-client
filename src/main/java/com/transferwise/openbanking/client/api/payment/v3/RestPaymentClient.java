package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.payment.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteFundsConfirmationResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.jwt.JwtClaimsSigner;
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

    private final IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator;
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestPaymentClient(RestOperations restOperations,
                             JsonConverter jsonConverter,
                             IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator,
                             JwtClaimsSigner jwtClaimsSigner) {
        super(restOperations, jsonConverter);
        this.idempotencyKeyGenerator = idempotencyKeyGenerator;
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    @Override
    public OBWriteDomesticConsentResponse5 createDomesticPaymentConsent(OBWriteDomesticConsent4 domesticPaymentConsentRequest,
                                                                        String clientCredentialsToken,
                                                                        AspspDetails aspspDetails,
                                                                        SoftwareStatementDetails softwareStatementDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            clientCredentialsToken,
            idempotencyKeyGenerator.generateKeyForSetup(domesticPaymentConsentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        String body = jsonConverter.writeValueAsString(domesticPaymentConsentRequest);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.info("Calling create payment consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(generateApiUrl(aspspDetails, PAYMENT_CONSENT_RESOURCE),
                HttpMethod.POST,
                request,
                String.class);
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to create payment consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to create payment consent endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticConsentResponse5.class);
        validateResponse(domesticPaymentConsentResponse);

        return domesticPaymentConsentResponse;
    }

    @Override
    public OBWriteDomesticResponse5 submitDomesticPayment(OBWriteDomestic2 domesticPaymentRequest,
                                                          String authorizationCodeToken,
                                                          AspspDetails aspspDetails,
                                                          SoftwareStatementDetails softwareStatementDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            authorizationCodeToken,
            idempotencyKeyGenerator.generateKeyForSubmission(domesticPaymentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentRequest, aspspDetails, softwareStatementDetails));

        String body = jsonConverter.writeValueAsString(domesticPaymentRequest);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.info("Calling submit payment API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_RESOURCE),
                HttpMethod.POST,
                request,
                String.class);
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to submit payment endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticResponse5 domesticPaymentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticResponse5.class);
        validateResponse(domesticPaymentResponse);

        return domesticPaymentResponse;
    }

    @Override
    public OBWriteDomesticConsentResponse5 getDomesticPaymentConsent(String consentId,
                                                                     String clientCredentialsToken,
                                                                     AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            clientCredentialsToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_CONSENT_RESOURCE) + "/{consentId}",
                HttpMethod.GET,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get payment consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get payment consent endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticConsentResponse5.class);
        validateResponse(domesticPaymentConsentResponse);

        return domesticPaymentConsentResponse;
    }

    @Override
    public OBWriteDomesticResponse5 getDomesticPayment(String domesticPaymentId,
                                                       String clientCredentialsToken,
                                                       AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            clientCredentialsToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_RESOURCE) + "/{domesticPaymentId}",
                HttpMethod.GET,
                request,
                String.class,
                domesticPaymentId);
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get payment endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticResponse5 domesticPaymentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticResponse5.class);
        validateResponse(domesticPaymentResponse);

        return domesticPaymentResponse;
    }

    @Override
    public OBWriteFundsConfirmationResponse1 getFundsConfirmation(String consentId,
                                                                  String authorizationCodeToken,
                                                                  AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            authorizationCodeToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get confirmation of funds API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(aspspDetails, PAYMENT_CONSENT_RESOURCE) + "/{consentId}/funds-confirmation",
                HttpMethod.GET,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            throw new ApiCallException("Call to get confirmation of funds endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e);
        } catch (RestClientException e) {
            throw new ApiCallException("Call to get confirmation of funds endpoint failed, and no response body returned",
                e);
        }

        OBWriteFundsConfirmationResponse1 fundsConfirmationResponse = jsonConverter.readValue(response.getBody(),
            OBWriteFundsConfirmationResponse1.class);
        validateResponse(fundsConfirmationResponse);

        return fundsConfirmationResponse;
    }

    private String generateApiUrl(AspspDetails aspspDetails, String resource) {
        return String.format(ENDPOINT_PATH_FORMAT,
            aspspDetails.getApiBaseUrl("3", resource),
            aspspDetails.getPaymentApiMinorVersion(),
            resource);
    }

    private void validateResponse(OBWriteDomesticConsentResponse5 response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getConsentId() == null ||
            response.getData().getConsentId().isBlank()) {
            throw new ApiCallException("Empty or partial domestic payment consent response returned " + response);
        }
    }

    private void validateResponse(OBWriteDomesticResponse5 response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getDomesticPaymentId() == null ||
            response.getData().getDomesticPaymentId().isBlank()) {
            throw new ApiCallException("Empty or partial domestic payment response returned " + response);
        }
    }

    private void validateResponse(OBWriteFundsConfirmationResponse1 response) {
        if (response == null || response.getData() == null) {
            throw new ApiCallException("Empty or partial funds confirmation response returned " + response);
        }
    }
}
