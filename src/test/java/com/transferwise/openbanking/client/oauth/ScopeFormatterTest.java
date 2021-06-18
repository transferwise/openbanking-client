package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.oauth.domain.Scope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings({"PMD.UnusedPrivateMethod"}) // PMD considers argumentsForFormatScopesTest unused
class ScopeFormatterTest {

    @ParameterizedTest
    @MethodSource("argumentsForFormatScopesTest")
    void formatScopes(Collection<Scope> scopes, String expectedFormattedValue) {
        Assertions.assertEquals(expectedFormattedValue, ScopeFormatter.formatScopes(scopes));
    }

    private static Stream<Arguments> argumentsForFormatScopesTest() {
        return Stream.of(
            Arguments.of(List.of(), ""),
            Arguments.of(List.of(Scope.PAYMENTS), "payments"),
            Arguments.of(List.of(Scope.OPENID, Scope.PAYMENTS), "openid payments")
        );
    }
}
