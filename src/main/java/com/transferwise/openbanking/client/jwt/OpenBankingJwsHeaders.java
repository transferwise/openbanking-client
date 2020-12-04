package com.transferwise.openbanking.client.jwt;

import lombok.experimental.UtilityClass;

/**
 * Defines the custom JWS header names used in the Open Banking standard.
 */
@UtilityClass
public class OpenBankingJwsHeaders {

    public static final String OPEN_BANKING_IAT = "http://openbanking.org.uk/iat";
    public static final String OPEN_BANKING_ISS = "http://openbanking.org.uk/iss";
    public static final String OPEN_BANKING_TAN = "http://openbanking.org.uk/tan";
}
