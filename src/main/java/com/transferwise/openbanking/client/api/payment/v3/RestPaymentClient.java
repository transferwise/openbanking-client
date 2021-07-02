package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.ApiResponse;
import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.common.BaseClient;
import com.transferwise.openbanking.client.api.payment.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.OBErrorResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.OBWriteFundsConfirmationResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
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
public class RestPaymentClient extends BaseClient implements PaymentClient {

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
    public ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> createDomesticPaymentConsent(
        OBWriteDomesticConsent4 domesticPaymentConsentRequest,
        String clientCredentialsToken,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {

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
            return mapClientExceptionWithResponse(e, OBErrorResponse1.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticConsentResponse5.class);
        if (isResponseInvalid(domesticPaymentConsentResponse)) {
            return mapInvalidResponse(response);
        }

        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), domesticPaymentConsentResponse);
    }

    @Override
    public ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> submitDomesticPayment(
        OBWriteDomestic2 domesticPaymentRequest,
        String authorizationCodeToken,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails
    ) {

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
            return mapClientExceptionWithResponse(e, OBErrorResponse1.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        OBWriteDomesticResponse5 domesticPaymentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticResponse5.class);
        if (isResponseInvalid(domesticPaymentResponse)) {
            return mapInvalidResponse(response);
        }

        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), domesticPaymentResponse);
    }

    @Override
    public ApiResponse<OBWriteDomesticConsentResponse5, OBErrorResponse1> getDomesticPaymentConsent(
        String consentId,
        String clientCredentialsToken,
        AspspDetails aspspDetails
    ) {

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
            return mapClientExceptionWithResponse(e, OBErrorResponse1.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticConsentResponse5.class);
        if (isResponseInvalid(domesticPaymentConsentResponse)) {
            return mapInvalidResponse(response);
        }

        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), domesticPaymentConsentResponse);
    }

    @Override
    public ApiResponse<OBWriteDomesticResponse5, OBErrorResponse1> getDomesticPayment(String domesticPaymentId,
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
            return mapClientExceptionWithResponse(e, OBErrorResponse1.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        OBWriteDomesticResponse5 domesticPaymentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticResponse5.class);
        if (isResponseInvalid(domesticPaymentResponse)) {
            return mapInvalidResponse(response);
        }

        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), domesticPaymentResponse);
    }

    @Override
    public ApiResponse<OBWriteFundsConfirmationResponse1, OBErrorResponse1> getFundsConfirmation(
        String consentId,
        String authorizationCodeToken,
        AspspDetails aspspDetails
    ) {

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
            return mapClientExceptionWithResponse(e, OBErrorResponse1.class);
        } catch (RestClientException e) {
            return mapClientException(e);
        }

        OBWriteFundsConfirmationResponse1 fundsConfirmationResponse = jsonConverter.readValue(response.getBody(),
            OBWriteFundsConfirmationResponse1.class);
        if (isResponseInvalid(fundsConfirmationResponse)) {
            return mapInvalidResponse(response);
        }

        return ApiResponse.success(response.getStatusCodeValue(), response.getBody(), fundsConfirmationResponse);
    }

    private String generateApiUrl(AspspDetails aspspDetails, String resource) {
        return String.format(ENDPOINT_PATH_FORMAT,
            aspspDetails.getApiBaseUrl("3", resource),
            aspspDetails.getPaymentApiMinorVersion(),
            resource);
    }

    private boolean isResponseInvalid(OBWriteDomesticConsentResponse5 response) {
        return response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getConsentId() == null ||
            response.getData().getConsentId().isBlank();
    }

    private boolean isResponseInvalid(OBWriteDomesticResponse5 response) {
        return response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getDomesticPaymentId() == null ||
            response.getData().getDomesticPaymentId().isBlank();
    }

    private boolean isResponseInvalid(OBWriteFundsConfirmationResponse1 response) {
        return response == null || response.getData() == null;
    }
}
