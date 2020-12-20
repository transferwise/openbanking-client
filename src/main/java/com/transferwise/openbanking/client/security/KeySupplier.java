package com.transferwise.openbanking.client.security;

import com.transferwise.openbanking.client.configuration.AspspDetails;

import java.security.Key;
import java.security.cert.Certificate;

/**
 * An interface specifying the operations for supplying keys and certificates to use, as part of sending requests to
 * ASPSPs.
 */
public interface KeySupplier {

    /**
     * Get the key to use for signing data to send to an ASPSP.
     *
     * @param aspspDetails The details of the ASPSP the data will be sent to
     * @return The signing key
     */
    Key getSigningKey(AspspDetails aspspDetails);

    /**
     * Get the certificate to use for signing data to send to an ASPSP.
     *
     * @param aspspDetails The details of the ASPSP the data will be sent to
     * @return The signing certificate
     */
    Certificate getSigningCertificate(AspspDetails aspspDetails);

    /**
     * Get the certificate to use for the secure (TLS) connection to an ASPSP.
     *
     * @param aspspDetails The details of the ASPSP the connection will be made to
     * @return The transport certificate
     */
    Certificate getTransportCertificate(AspspDetails aspspDetails);
}
