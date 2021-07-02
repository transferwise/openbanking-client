package com.transferwise.openbanking.client.api.common;

import com.transferwise.openbanking.client.error.ApiCallException;
import com.transferwise.openbanking.client.json.JsonConverter;
import com.transferwise.openbanking.client.json.JsonReadException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;

/**
 * Base class for all API clients, containing common functionality.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class BaseClient {

    protected final RestOperations restOperations;
    protected final JsonConverter jsonConverter;

    protected <T, U> ApiResponse<T, U> mapClientExceptionWithResponse(RestClientResponseException e,
                                                                      Class<U> errorResponseType) {
        String responseBody = e.getResponseBodyAsString();
        U parsedFailureResponse = null;
        try {
            parsedFailureResponse = jsonConverter.readValue(responseBody, errorResponseType);
        } catch (JsonReadException jsonReadException) {
            log.info("Unable to parse failure response body JSON '{}'", jsonReadException.getJson(), jsonReadException);
        }

        return ApiResponse.failure(e.getRawStatusCode(), responseBody, e, parsedFailureResponse);
    }

    protected <T, U> ApiResponse<T, U> mapClientException(RestClientException e) {
        return ApiResponse.failure(e);
    }

    protected <T, U> ApiResponse<T, U> mapInvalidResponse(ResponseEntity<String> response) {
        return ApiResponse.failure(response.getStatusCodeValue(),
            response.getBody(),
            new ApiCallException("Empty or partial response returned"));
    }
}
