package com.transferwise.openbanking.client.api.common;

import com.transferwise.openbanking.client.oauth.domain.FapiHeaders;
import org.springframework.http.MediaType;

/**
 * An extension of the Spring {@link org.springframework.http.HttpHeaders} data structure, containing functionality
 * for setting common HTTP headers for Open Banking API requests.
 */
public class OpenBankingHeaders extends FapiHeaders {

    private static final String IDEMPOTENCY_KEY = "x-idempotency-key";

    /**
     * Builds the HTTP headers common for all API requests.
     *
     * @param financialId The ASPSP financial ID value to use as the x-fapi-financial-id header value
     * @param bearerToken The bearer token to use for the Authorization header value
     * @return The built HTTP headers
     */
    public static OpenBankingHeaders defaultHeaders(String financialId, String bearerToken) {
        OpenBankingHeaders headers = new OpenBankingHeaders();
        headers.setBaseValues(financialId, bearerToken);
        return headers;
    }

    /**
     * Builds the HTTP headers common for all POST API requests.
     *
     * @param financialId    The ASPSP financial ID value to use as the x-fapi-financial-id header value
     * @param bearerToken    The bearer token to use for the Authorization header value
     * @param idempotencyKey The idempotency key for the request, to use as the x-idempotency-key header value
     * @return The built HTTP headers
     */
    public static OpenBankingHeaders postHeaders(String financialId, String bearerToken, String idempotencyKey) {
        OpenBankingHeaders headers = defaultHeaders(financialId, bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setIdempotencyKey(idempotencyKey);
        return headers;
    }

    private void setBaseValues(String financialId, String bearerToken) {
        super.setBaseValues();

        setFinancialId(financialId);
        setBearerAuth(bearerToken);
    }

    private void setIdempotencyKey(String idempotencyKey) {
        set(IDEMPOTENCY_KEY, idempotencyKey);
    }
}
