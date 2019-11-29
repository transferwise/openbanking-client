package com.transferwise.openbanking.client.api.common;

import com.transferwise.openbanking.client.oauth.domain.FapiHeaders;

import org.springframework.http.MediaType;

public class OpenBankingHeaders extends FapiHeaders {

    private static final String IDEMPOTENCY_KEY = "x-idempotency-key";

    private void setBaseValues(String financialId, String bearerToken) {
        super.setBaseValues();

        setFinancialId(financialId);
        setBearerAuth(bearerToken);
    }

    private void setIdempotencyKey(String idempotencyKey) {
        set(IDEMPOTENCY_KEY, idempotencyKey);
    }

    public static OpenBankingHeaders defaultHeaders(String financialId, String bearerToken) {
        OpenBankingHeaders headers = new OpenBankingHeaders();
        headers.setBaseValues(financialId, bearerToken);
        return headers;
    }

    public static OpenBankingHeaders postHeaders(String financialId, String bearerToken, String idempotencyKey) {
        OpenBankingHeaders headers = defaultHeaders(financialId, bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setIdempotencyKey(idempotencyKey);
        return headers;
    }
}
