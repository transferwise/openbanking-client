package com.transferwise.openbanking.client.oauth;

import com.transferwise.openbanking.client.oauth.domain.Scope;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScopeFormatter {

    /**
     * Format a list of scopes as a single space seperated string, according to the RFC6749 OAuth 2 standard.
     *
     * @param scopes The list of scopes to format
     * @return The scopes formatted as a single string
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">RFC6749</a>
     */
    public static String formatScopes(Collection<Scope> scopes) {
        return scopes.stream()
            .map(Scope::getValue)
            .collect(Collectors.joining(" "));
    }
}
