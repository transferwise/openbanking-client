package com.transferwise.openbanking.client.api.payment.v3;

import com.transferwise.openbanking.client.api.common.OpenBankingHeaders;
import com.transferwise.openbanking.client.api.common.AuthorizationContext;
import com.transferwise.openbanking.client.api.common.BasePaymentClient;
import com.transferwise.openbanking.client.api.common.IdempotencyKeyGenerator;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsent4;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsentResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteFundsConfirmationResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBErrorResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.json.JsonConverter;
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

    private final IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator;
    private final JwtClaimsSigner jwtClaimsSigner;

    public RestPaymentClient(RestOperations restOperations,
                             JsonConverter jsonConverter,
                             OAuthClient oAuthClient,
                             IdempotencyKeyGenerator<OBWriteDomesticConsent4, OBWriteDomestic2> idempotencyKeyGenerator,
                             JwtClaimsSigner jwtClaimsSigner) {
        super(restOperations, jsonConverter, oAuthClient);
        this.idempotencyKeyGenerator = idempotencyKeyGenerator;
        this.jwtClaimsSigner = jwtClaimsSigner;
    }

    @Override
    public OBWriteDomesticConsentResponse5 createDomesticPaymentConsent(OBWriteDomesticConsent4 domesticPaymentConsentRequest,
                                                                        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails),
            idempotencyKeyGenerator.generateKeyForSetup(domesticPaymentConsentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentConsentRequest,
                aspspDetails,
                softwareStatementDetails));

        String body = jsonConverter.writeValueAsString(domesticPaymentConsentRequest);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.info("Calling create payment consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_CONSENT_RESOURCE, aspspDetails),
                HttpMethod.POST,
                request,
                String.class);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new PaymentApiCallException("Call to create payment consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new PaymentApiCallException("Call to create payment consent endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = response.getBody() != null ? jsonConverter.readValue(response.getBody(),
            OBWriteDomesticConsentResponse5.class) : null;
        validateResponse(domesticPaymentConsentResponse);

        return domesticPaymentConsentResponse;
    }

    @Override
    public OBWriteDomesticResponse5 submitDomesticPayment(OBWriteDomestic2 domesticPaymentRequest,
                                                          AuthorizationContext authorizationContext,
                                                          AspspDetails aspspDetails,
                                                          SoftwareStatementDetails softwareStatementDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.postHeaders(aspspDetails.getOrganisationId(),
            exchangeAuthorizationCode(authorizationContext, aspspDetails).getAccessToken(),
            idempotencyKeyGenerator.generateKeyForSubmission(domesticPaymentRequest),
            jwtClaimsSigner.createDetachedSignature(domesticPaymentRequest, aspspDetails, softwareStatementDetails));

        String body = jsonConverter.writeValueAsString(domesticPaymentRequest);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        log.info("Calling submit payment API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_RESOURCE, aspspDetails),
                HttpMethod.POST,
                request,
                String.class);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new PaymentApiCallException("Call to submit payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new PaymentApiCallException("Call to submit payment endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticResponse5 domesticPaymentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticResponse5.class);
        validateResponse(domesticPaymentResponse);

        return domesticPaymentResponse;
    }

    @Override
    public OBWriteDomesticConsentResponse5 getDomesticPaymentConsent(String consentId, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment consent API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_CONSENT_RESOURCE, aspspDetails) + "/{consentId}",
                HttpMethod.GET,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new PaymentApiCallException("Call to get payment consent endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new PaymentApiCallException("Call to get payment consent endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticConsentResponse5 domesticPaymentConsentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticConsentResponse5.class);
        validateResponse(domesticPaymentConsentResponse);

        return domesticPaymentConsentResponse;
    }

    @Override
    public OBWriteDomesticResponse5 getDomesticPayment(String domesticPaymentId, AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(aspspDetails.getOrganisationId(),
            getClientCredentialsToken(aspspDetails));

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get payment API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_RESOURCE, aspspDetails) + "/{domesticPaymentId}",
                HttpMethod.GET,
                request,
                String.class,
                domesticPaymentId);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new PaymentApiCallException("Call to get payment endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new PaymentApiCallException("Call to get payment endpoint failed, and no response body returned", e);
        }

        OBWriteDomesticResponse5 domesticPaymentResponse = jsonConverter.readValue(response.getBody(),
            OBWriteDomesticResponse5.class);
        validateResponse(domesticPaymentResponse);

        return domesticPaymentResponse;
    }

    @Override
    public OBWriteFundsConfirmationResponse1 getFundsConfirmation(String consentId,
                                                                  AuthorizationContext authorizationContext,
                                                                  AspspDetails aspspDetails) {

        OpenBankingHeaders headers = OpenBankingHeaders.defaultHeaders(
            aspspDetails.getOrganisationId(),
            exchangeAuthorizationCode(authorizationContext, aspspDetails).getAccessToken());

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling get confirmation of funds API, with interaction ID {}", headers.getInteractionId());

        ResponseEntity<String> response;
        try {
            response = restOperations.exchange(
                generateApiUrl(ENDPOINT_PATH_FORMAT, PAYMENT_CONSENT_RESOURCE, aspspDetails) + "/{consentId}/funds-confirmation",
                HttpMethod.GET,
                request,
                String.class,
                consentId);
        } catch (RestClientResponseException e) {
            OBErrorResponse1 errorResponse = mapBodyToObErrorResponse(e.getResponseBodyAsString());
            throw new PaymentApiCallException("Call to get confirmation of funds endpoint failed, body returned '" + e.getResponseBodyAsString() + "'",
                e,
                errorResponse);
        } catch (RestClientException e) {
            throw new PaymentApiCallException("Call to get confirmation of funds endpoint failed, and no response body returned",
                e);
        }

        OBWriteFundsConfirmationResponse1 fundsConfirmationResponse = jsonConverter.readValue(response.getBody(),
            OBWriteFundsConfirmationResponse1.class);
        validateResponse(fundsConfirmationResponse);

        return fundsConfirmationResponse;
    }

    private void validateResponse(OBWriteDomesticConsentResponse5 response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getConsentId() == null ||
            response.getData().getConsentId().isBlank()) {
            throw new PaymentApiCallException("Empty or partial domestic payment consent response returned " + response);
        }
    }

    private void validateResponse(OBWriteDomesticResponse5 response) {
        if (response == null ||
            response.getData() == null ||
            response.getData().getStatus() == null ||
            response.getData().getDomesticPaymentId() == null ||
            response.getData().getDomesticPaymentId().isBlank()) {
            throw new PaymentApiCallException("Empty or partial domestic payment response returned " + response);
        }
    }

    private void validateResponse(OBWriteFundsConfirmationResponse1 response) {
        if (response == null || response.getData() == null) {
            throw new PaymentApiCallException("Empty or partial funds confirmation response returned " + response);
        }
    }

    private OBErrorResponse1 mapBodyToObErrorResponse(String responseBodyAsString) {
        try {
            return jsonConverter.readValue(responseBodyAsString, OBErrorResponse1.class);
        } catch (Exception ex) {
            return null;
        }
    }
}
