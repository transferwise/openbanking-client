package com.transferwise.openbanking.client.test;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class TestKeyUtils {

    public static KeyPair aKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static X509Certificate aCertificate() throws Exception {
        return aCertificate(aKeyPair());
    }

    public static X509Certificate aCertificate(KeyPair keyPair) throws Exception {
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
            new X500Name("CN=ISSUER_COMMON_NAME,O=ISSUER_ORGANISATION,C=GB"),
            BigInteger.ONE,
            Date.from(LocalDateTime.now().minusWeeks(1).toInstant(ZoneOffset.UTC)),
            Date.from(LocalDateTime.now().plusWeeks(1).toInstant(ZoneOffset.UTC)),
            new X500Name("CN=ORGANISATION_ID,O=ORGANISATION_NAME,C=GB"),
            keyPair.getPublic());
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA")
            .build(keyPair.getPrivate());
        return new JcaX509CertificateConverter()
            .setProvider(new BouncyCastleProvider())
            .getCertificate(certificateBuilder.build(contentSigner));
    }
}
