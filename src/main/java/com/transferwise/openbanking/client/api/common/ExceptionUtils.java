package com.transferwise.openbanking.client.api.common;

import com.transferwise.openbanking.client.error.ApiCallException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@UtilityClass
@Slf4j
public class ExceptionUtils {

    public static <T> Mono<T> handleWebClientResponseException(WebClientResponseException e, String prefixLog) {
        var errorMessage = "%s, response status code %s, body returned '%s'".formatted(prefixLog, e.getStatusCode(), e.getResponseBodyAsString());
        log.error(errorMessage, e);
        return Mono.error(new ApiCallException(errorMessage, e));
    }

    public static <T, E extends Exception> Mono<T> handleWebClientException(
        WebClientException e,
        String prefixLog,
        Class<E> exceptionClass
    ) {
        var errorMessage = "%s, and no response body returned".formatted(prefixLog);
        log.error(errorMessage, e);
        try {
            return Mono.error(exceptionClass.getConstructor(String.class, Throwable.class).newInstance(errorMessage, e));
        } catch (Exception ex) {
            return Mono.error(new ApiCallException(errorMessage, e));
        }
    }
}
