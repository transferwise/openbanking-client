package com.transferwise.openbanking.client.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A container for the response to an API call to an ASPSP, which may be a successful response or a failure response.
 *
 * @param <T> The type returned in the response body, when the API call is successful
 * @param <U> The type returned in the response body, when the API call is un-successful, and a structured error
 *            response is returned
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T, U> {

    private static final int CLIENT_ERROR_STATUS_START_RANGE = 400;
    private static final int SERVER_ERROR_STATUS_START_RANGE = 500;

    /**
     * The HTTP status code returned. May be null in case the API failed and no response was returned.
     */
    protected Integer statusCode;
    /**
     * The raw HTTP response body returned. May be null in case the API failed and no response was returned.
     */
    protected String responseBody;

    /**
     * Whether or not the API call is considered failed in some way.
     */
    protected boolean callFailed;
    /**
     * When the API call failed, the triggering exception containing more context of the failure. May be null in case
     * the API call failure was not caught via an exception.
     */
    protected Throwable failureException;

    /**
     * When the API call succeeded, the parsed response body.
     */
    protected T successResponseBody;
    /**
     * When the API call failed, the parsed response body. May be null in case the API call failed but a structured
     * error response was not returned.
     */
    protected U failureResponseBody;

    /**
     * Whether or not the API call failed, and a 4xx status code was returned.
     */
    public boolean isClientErrorResponse() {
        return statusCode >= CLIENT_ERROR_STATUS_START_RANGE && statusCode < SERVER_ERROR_STATUS_START_RANGE;
    }

    /**
     * Whether or not the API call failed, and a 5xx status code was returned.
     */
    public boolean isServerErrorResponse() {
        return statusCode >= SERVER_ERROR_STATUS_START_RANGE;
    }

    public static <T, U> ApiResponse<T, U> success(int statusCode, String responseBody, T parsedSuccessfulResponse) {
        return new ApiResponse<>(statusCode, responseBody, false, null, parsedSuccessfulResponse, null);
    }

    public static <T, U> ApiResponse<T, U> failure(int statusCode,
                                                   String responseBody,
                                                   Throwable failureCause,
                                                   U parsedFailureResponse) {
        return new ApiResponse<>(statusCode, responseBody, true, failureCause, null, parsedFailureResponse);
    }

    public static <T, U> ApiResponse<T, U> failure(int statusCode,
                                                   String responseBody,
                                                   Throwable failureCause) {
        return new ApiResponse<>(statusCode, responseBody, true, failureCause, null, null);
    }

    public static <T, U> ApiResponse<T, U> failure(Throwable failureCause) {
        return new ApiResponse<>(null, null, true, failureCause, null, null);
    }
}
