package com.github.iarellano.rest_client.security.ssl;

import com.github.iarellano.rest_client.configuration.SSLInfo;

import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class KeyManagerImpl implements X509KeyManager {

    private final SSLInfo sslInfo;

    private final KeyStore keyStore;

    public KeyManagerImpl(SSLInfo sslInfo, KeyStore keyStore) {
        this.sslInfo = sslInfo;
        this.keyStore = keyStore;
    }

    @Override
    public String[] getClientAliases(String s, Principal[] principals) {
        return new String[]{sslInfo.getClientAlias()};
    }

    @Override
    public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
        return sslInfo.getClientAlias();
    }

    @Override
    public String[] getServerAliases(String s, Principal[] principals) {
        return sslInfo.getServerAlias() == null
                ? new String[0]
                : new String[]{sslInfo.getServerAlias()};
    }

    @Override
    public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
        return sslInfo.getServerAlias();
    }

    @Override
    public X509Certificate[] getCertificateChain(String s) {
        try {
            java.security.cert.Certificate[] certificates = keyStore.getCertificateChain(sslInfo.getClientAlias());
            X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
            for (int i = 0; i < certificates.length; i++) {
                x509Certificates[i] = (X509Certificate) certificates[i];
            }
            return x509Certificates;
        } catch (Exception e) {
            throw new RuntimeException("Could not get certificate chain", e);
        }
    }

    @Override
    public PrivateKey getPrivateKey(String s) {
        try {
            return (PrivateKey) keyStore.getKey(sslInfo.getClientAlias(), sslInfo.getKeystorePasswordAsCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Could not extract private key from keystore", e);
        }
    }
}
