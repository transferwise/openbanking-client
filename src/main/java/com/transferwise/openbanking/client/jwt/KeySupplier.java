package com.transferwise.openbanking.client.jwt;

import com.transferwise.openbanking.client.configuration.AspspDetails;

import java.security.Key;

/**
 * An interface specifying the operations for supplying the key to use, as part of sending requests to ASPSPs.
 */
public interface KeySupplier {

    /**
     * Get the key to use for signing data to send to an ASPSP.
     *
     * @param aspspDetails The details of the ASPSP the data will be sent to
     * @return The signing key
     */
    Key getSigningKey(AspspDetails aspspDetails);
}
