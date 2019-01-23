package com.github.iarellano.rest_client.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public class SSLInfo {

    @Parameter
    private String type;

    @Parameter
    private File keystore;

    @Parameter
    private String keystorePassword;

    @Parameter
    private File truststore;

    @Parameter
    private String truststorePassword;

    @Parameter
    private String clientAlias;

    @Parameter
    private String serverAlias;

    @Parameter
    private String protocol;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public File getKeystore() {
        return keystore;
    }

    public void setKeystore(File keystore) {
        this.keystore = keystore;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public File getTruststore() {
        return truststore;
    }

    public void setTruststore(File truststore) {
        this.truststore = truststore;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }

    public void setTruststorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
    }

    public String getClientAlias() {
        return clientAlias;
    }

    public void setClientAlias(String clientAlias) {
        this.clientAlias = clientAlias;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServerAlias() {
        return serverAlias;
    }

    public void setServerAlias(String serverAlias) {
        this.serverAlias = serverAlias;
    }

    public char[] getKeystorePasswordAsCharArray() {
        return keystorePassword == null
                ? null
                : keystorePassword.toCharArray();
    }

    public char[] getTrustStorePasswordAsCharArray() {
        return truststorePassword == null
                ? null
                : truststorePassword.toCharArray();
    }


}
