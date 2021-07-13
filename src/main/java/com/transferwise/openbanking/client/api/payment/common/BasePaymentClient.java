package com.transferwise.openbanking.client.api.payment.common;

import com.transferwise.openbanking.client.json.JsonConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestOperations;

/**
 * Base class for all payments API clients, containing common functionality.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BasePaymentClient {

    protected final RestOperations restOperations;
    protected final JsonConverter jsonConverter;
}
