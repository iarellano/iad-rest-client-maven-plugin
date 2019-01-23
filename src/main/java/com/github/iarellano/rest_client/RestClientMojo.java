package com.github.iarellano.rest_client;


import com.github.iarellano.rest_client.configuration.*;
import com.github.iarellano.rest_client.configuration.Proxy;
import com.github.iarellano.rest_client.cookie.CookieUtil;
import com.github.iarellano.rest_client.extract.json.JsonProperty;
import com.github.iarellano.rest_client.extract.xml.ExtractXml;
import com.github.iarellano.rest_client.extract.xml.XmlProperty;
import com.github.iarellano.rest_client.security.ssl.KeyManagerImpl;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.codec.binary.Base64;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;
import org.w3c.dom.Document;

import javax.net.ssl.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "http-request", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class RestClientMojo
        extends AbstractMojo {

    @Parameter(required = true, defaultValue = "GET")
    private String method;

    @Parameter(required = true)
    private String resourceUrl;

    @Parameter
    private Map<String, String> headers = new HashMap<>();

    @Parameter
    private List<QueryParam> queryParams = new ArrayList<>();

    @Parameter
    private File responseOutput;

    @Parameter
    private File headersOutput;

    @Parameter(defaultValue = "")
    private String headersPrefix;

    @Parameter
    private File file;

    @Parameter
    private String payload;

    @Parameter
    private BasicCredentials authorization;

    @Parameter
    private Map<String, String> pathParameters = new HashMap<>();

    @Parameter
    private Proxy proxy;

    @Parameter(defaultValue = "true")
    private boolean validateServerCert;

    @Parameter
    private List<FormInput> form = new ArrayList<>();

    @Parameter
    private List<MultipartInput> multipart = new ArrayList<>();

    @Parameter
    private List<JsonProperty> extractJson = new ArrayList<>();

    @Parameter
    private ExtractXml extractXml = null;

    @Parameter(defaultValue = "200,201,202,204")
    private String successCodes;

    @Parameter(defaultValue = "UTF-8")
    private String charset;

    @Parameter
    private SSLInfo sslConfig;

    @Parameter(defaultValue = "false")
    private boolean followRedirect;

    @Parameter(defaultValue = "false")
    private boolean loadHeadersToSysProperties;

    @Parameter(defaultValue = "")
    private String loadHeadersPrefix;

    @Parameter(defaultValue = "")
    private String extractPrefix;

    @Parameter
    private String responseCharset = null;

    @Parameter
    private CookieConfig cookieConfig = null;

    @Parameter(defaultValue = "0")
    private int connectTimeout;

    @Parameter(defaultValue = "0")
    private int readTimeout;

    private HostnameVerifier defaultHostNameVerifier;

    public void execute() throws MojoExecutionException {

        final String resourcePath = resolvePathVariables(resourceUrl);
        final String targetUrl = resourcePath + buildQueryString();
        final java.net.Proxy httpProxy = configureProxxy(proxy);
        final boolean defaultFollowRedirect = HttpURLConnection.getFollowRedirects();
        Properties responseHeaders = null;
        HttpURLConnection urlConnection = null;

        CookieUtil cookieUtil = null;
        try {

            if (cookieConfig != null) {
                try {
                    cookieUtil = new CookieUtil(cookieConfig, this.getLog());
                    cookieUtil.installCookieManager();
                } catch (IOException ioe) {
                    throw new MojoExecutionException("Could not initialize cookie store", ioe);
                }
            }
            URL url = new URL(targetUrl);
            if ("https".equals(url.getProtocol()) && !validateServerCert) {
                installSslSkipVerification();
                defaultHostNameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
            }

            HttpURLConnection.setFollowRedirects(followRedirect);
            urlConnection = (HttpURLConnection) url.openConnection(httpProxy);
            urlConnection.setInstanceFollowRedirects(followRedirect);
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);

            SSLContext sc = null;
            if (sslConfig != null) {
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(createSSLSocketFactory());
            }
            urlConnection.setRequestMethod(method);
            setRequestHeaders(urlConnection);
            setAthorizationHeader(urlConnection);
            setProxyAuthorizationHeader(urlConnection);
            if (responseOutput != null) {
                urlConnection.setDoInput(true);
            }

            if (form.size() > 0) {
                attachFormData(urlConnection);
            } else if (multipart.size() > 0) {
                attachMultipartData(urlConnection);
            } else if (file != null) {
                attachFile(urlConnection);
            } else if (payload != null) {
                attachPayload(urlConnection);
            } else {
                urlConnection.setDoOutput(false);
                urlConnection.connect();
            }
            byte[] data = readResponseData(urlConnection.getInputStream());
            saveResponse(data);
            responseHeaders = extractResponseHeaders(urlConnection);
            storeResponseHeaders(responseHeaders, targetUrl);
            loadResponseHeadersAsProperties(responseHeaders);
            verifySuccessResponse(urlConnection);
            extractJsonProperties(data);
            extractXmlProperties(data);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Invalid target url " + targetUrl, e);
        } catch (SSLException ssle) {
            throw new MojoExecutionException("SSL Error", ssle);
        } catch (IOException e) {
            if (urlConnection != null) {
                byte[] data = readResponseData(urlConnection.getErrorStream());
                saveResponse(data);
                responseHeaders = extractResponseHeaders(urlConnection);
                storeResponseHeaders(responseHeaders, targetUrl);
                loadResponseHeadersAsProperties(responseHeaders);
                verifySuccessResponse(urlConnection);
                extractJsonProperties(data);
                extractXmlProperties(data);
            }
        } finally {
            if (defaultHostNameVerifier != null) {
                HttpsURLConnection.setDefaultHostnameVerifier(defaultHostNameVerifier);
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            HttpURLConnection.setFollowRedirects(defaultFollowRedirect);
            if (cookieUtil != null) {
                if (cookieConfig.getCookieJar() != null) {
                    try {
                        cookieUtil.persistCookies();
                    } catch (IOException e) {
                        throw new MojoExecutionException("Could not persist cookies to file system", e);
                    }
                }
                cookieUtil.restoreOriginalCookieManager();
            }
        }
    }

    private void extractXmlProperties(byte[] data) throws MojoExecutionException {
        if (extractXml != null) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(!extractXml.getNamespaces().isEmpty());
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new ByteArrayInputStream(data));
                XPath xpath = XPathFactory.newInstance().newXPath();
                if (!extractXml.getNamespaces().isEmpty()) {
                    xpath.setNamespaceContext(new NamespaceContext() {

                        @Override
                        public String getNamespaceURI(String prefix) {
                            return extractXml.getNamespaces().get(prefix);
                        }

                        @Override
                        public String getPrefix(String namespaceURI) {
                            return null;
                        }

                        @Override
                        public Iterator<String> getPrefixes(String namespaceURI) {
                            return null;
                        }
                    });
                }
                for (final XmlProperty xmlProperty : extractXml.getProperties()) {
                    try {
                        XPathExpression xPathExpr = xpath.compile(xmlProperty.getXpath());
                        String value = (String) xPathExpr.evaluate(document, XPathConstants.STRING);
                        String propertyName = extractPrefix + xmlProperty.getName();
                        System.setProperty(propertyName, value);
                        getLog().debug("XPath: " + xmlProperty.getXpath());
                        getLog().debug("    XPath Result: " + propertyName + "=" + value);
                    } catch (Exception e) {
                        if (xmlProperty.isFailFast()) {
                            throw new MojoExecutionException("Could not extract xpath " + xmlProperty.getXpath(), e);
                        }
                    }
                }
            } catch (Exception e) {
                throw new MojoExecutionException("Could not extract xpaths from response", e);
            }
        }
    }

    private void extractJsonProperties(byte[] data) throws MojoExecutionException {
        if (extractJson.size() > 0) {
            nsDetector det = new nsDetector(nsPSMDetector.ALL);
            if (this.responseCharset == null) {
                detectCharset(data);
            }
            Object document = this.responseCharset == null
                    ? Configuration.defaultConfiguration().jsonProvider().parse(new String(data))
                    : Configuration.defaultConfiguration().jsonProvider().parse(new ByteArrayInputStream(data), this.responseCharset);
            for (JsonProperty jsonProperty : extractJson) {
                try {
                    Object result = JsonPath.read(document, jsonProperty.getJsonPath());
                    String value = null;
                    if (result instanceof JSONArray) {
                        if (((JSONArray) result).isEmpty() && jsonProperty.isFailFast()) {
                            throw new MojoExecutionException("JsonPath '" + jsonProperty.getJsonPath() + "' did not produce any result");
                        } else if (((JSONArray) result).size() > 1 && jsonProperty.isFailFast()) {
                            throw new MojoExecutionException("JsonPath '" + jsonProperty.getJsonPath() + "' produced multiple results");
                        }
                        value = ((JSONArray) result).get(0).toString();
                    } else {
                        value = (String) result;
                    }
                    String propertyName = extractPrefix + jsonProperty.getName();
                    System.setProperty(propertyName, value);
                    getLog().debug("JSONPath: " + jsonProperty.getJsonPath());
                    getLog().debug("    JSONPath Result: " + propertyName + "=" + value);
                } catch (Exception e) {
                    if (jsonProperty.isFailFast()) {
                        throw new MojoExecutionException("Could not extract json path " + jsonProperty.getJsonPath(), e);
                    }
                }
            }
        }
    }

    private void loadResponseHeadersAsProperties(Properties responseHeaders) {
        if (loadHeadersToSysProperties == true) {
            for (String propName : responseHeaders.stringPropertyNames()) {
                System.setProperty(loadHeadersPrefix + propName, responseHeaders.getProperty(propName));
            }
        }
    }

    private void setProxyAuthorizationHeader(HttpURLConnection urlConnection) {
        if (proxy != null && proxy.getHost() != null && proxy.getUsername() != null) {
            String usernamePassword = basicAuthorization(proxy.getUsername(), proxy.getPassword());
            urlConnection.setRequestProperty("Proxy-Authorization", usernamePassword);
        }
    }

    private void verifySuccessResponse(HttpURLConnection urlConnection) throws MojoExecutionException {
        try {
            String responseCode = Integer.toString(urlConnection.getResponseCode());
            String[] successCodes = this.successCodes.split(",");
            for (String successCode : successCodes) {
                String regex = successCode.replaceAll("X", "\\\\d");
                if (responseCode.matches(regex)) {
                    return;
                }
            }
            throw new MojoExecutionException(String.format("Response code was expecting to be any of [%s] but '%s' was received instead", this.successCodes, responseCode));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private java.net.Proxy configureProxxy(Proxy proxy) {
        return (
                (proxy != null)
                        ? new java.net.Proxy(proxy.getType(), new InetSocketAddress(proxy.getHost(), proxy.getPort()))
                        : java.net.Proxy.NO_PROXY
        );
    }

    private String resolvePathVariables(String targetUrl) {
        for (String pathVariable : pathParameters.keySet()) {
            String pathSegment = pathParameters.get(pathVariable);
            targetUrl = targetUrl.replaceAll("\\{" + pathVariable + "}", pathSegment);
        }
        return targetUrl;
    }

    private String buildQueryString() throws MojoExecutionException {

        StringBuilder sb = new StringBuilder();
        if (queryParams.size() > 0) {
            sb.append("?");
            try {
                for (QueryParam queryParam : this.queryParams) {
                    sb.append(queryParam.getName());
                    sb.append("=");
                    sb.append(URLEncoder.encode(queryParam.getValue(), charset));
                    sb.append("&");
                }
                sb.replace(sb.length() - 1, sb.length(), "");
            } catch (UnsupportedEncodingException uee) {
                throw new MojoExecutionException("Could not encode query parameters", uee);
            }
        }
        return sb.toString();
    }

    private void setRequestHeaders(HttpURLConnection conn) {
        for (String key : this.headers.keySet()) {
            conn.setRequestProperty(key, headers.get(key));
        }
    }

    private void setAthorizationHeader(HttpURLConnection urlConnection) {
        if (authorization != null) {
            String basicAuth = basicAuthorization(authorization.getUsername(), authorization.getPassword());
            urlConnection.setRequestProperty("Authorization", basicAuth);
        }
    }

    private void attachFormData(HttpURLConnection urlConnection) throws MojoExecutionException {
        try {
            StringBuilder sb = new StringBuilder();
            if (form.size() > 0) {
                for (FormInput formInput : form) {
                    sb.append(formInput.getName());
                    sb.append("=");
                    sb.append(URLEncoder.encode(formInput.getValue(), charset));
                    sb.append("&");
                }
                sb.replace(sb.length() - 1, sb.length(), "");
            }

            String data = sb.toString();
            urlConnection.setRequestProperty("Content-Length", Integer.toString(data.length()));
            if (urlConnection.getRequestProperty("Content-Type") != null) {
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(data.getBytes());
            urlConnection.getOutputStream().close();
        } catch (UnsupportedEncodingException uee) {
            throw new MojoExecutionException("Could not encode post data for " + urlConnection.getURL(), uee);
        } catch (IOException ioe) {
            throw new MojoExecutionException("Could not post data to " + urlConnection.getURL(), ioe);
        }
    }

    private void attachMultipartData(HttpURLConnection conn) throws MojoExecutionException {
        final String fileContentDisp = "Content-Disposition: form-data; name=\"%1$s\"; filename=\"%2$s\"";
        final String fieldContentDisp = "Content-Disposition: form-data; name=\"%s\"";
        final String CRLF = "\r\n";
        final String multipartBoundary = createMultipartBoundary();

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(os);
            for (MultipartInput input : multipart) {
                pw.append(multipartBoundary);
                pw.append(CRLF);
                if (input.getFile() != null) {
                    InputStream is = new FileInputStream(input.getFile());
                    byte[] data = getFileContent(is);
                    pw.append(String.format(fileContentDisp, input.getName(), input.getFile().getName()));
                    pw.append(CRLF);
                    String mimetype = input.getContentType() != null
                            ? input.getContentType()
                            : HttpURLConnection.guessContentTypeFromName(input.getFile().getName());
                    if (mimetype == null) {
                        InputStream tmpIs = new ByteArrayInputStream(data);
                        mimetype = HttpURLConnection.guessContentTypeFromStream(tmpIs);
                        tmpIs.close();
                    }
                    pw.append("Content-Type: " + mimetype);
                    pw.append(CRLF).append(CRLF);
                    pw.append(new String(data));
                    pw.append(CRLF);
                } else {
                    pw.append(String.format(fieldContentDisp, input.getName()));
                    pw.append(CRLF).append(CRLF);
                    pw.append(input.getValue());
                    pw.append(CRLF);
                }
            }
            pw.append(multipartBoundary + "--");
            pw.flush();
            byte[] content = os.toByteArray();
            String payload = new String(content);

            conn.setRequestProperty("Content-Length", Integer.toString(payload.length()));
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + multipartBoundary.substring(2));
            conn.setDoOutput(true);
            conn.getOutputStream().write(content);
            conn.getOutputStream().close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could write request data to channel", e);
        }

    }

    private String createMultipartBoundary() {
        return "----------------------------" + UUID.randomUUID().toString();
    }

    private byte[] getFileContent(InputStream is) throws MojoExecutionException {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return os.toByteArray();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not attach file to request", e);
        }
    }

    private void attachFile(HttpURLConnection urlConnection) throws MojoExecutionException {
        try {
            InputStream is = new FileInputStream(file);
            byte[] data = getFileContent(is);
            urlConnection.setRequestProperty("Content-Length", Integer.toString(new String(data).length()));
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(data);
            urlConnection.getOutputStream().close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not attach file to request", e);
        }
    }

    private void attachPayload(HttpURLConnection urlConnection) throws MojoExecutionException {
        try {
            urlConnection.setRequestProperty("Content-Length", Integer.toString(payload.length()));
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(payload.getBytes());
            urlConnection.getOutputStream().close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not attach payload to request", e);
        }
    }

    private Properties extractResponseHeaders(HttpURLConnection conn) {
        Properties properties = new Properties();
        for (String headerName : conn.getHeaderFields().keySet()) {
            if (headerName == null) continue;
            String headerValue = conn.getHeaderField(headerName);
            properties.setProperty(headerName, headerValue != null ? headerValue : "");
        }
        return properties;
    }

    private void storeResponseHeaders(Properties headers, String targetUrl) {
        if (headersOutput != null) {
            try {
                headersOutput.getParentFile().mkdirs();
                Properties prefixedHeaders = new Properties();
                for (Object key : headers.keySet()) {
                    prefixedHeaders.setProperty(headersPrefix + ((String) key), headers.getProperty((String) key));
                }
                prefixedHeaders.store(new FileOutputStream(headersOutput), " Header properties from " + targetUrl);
            } catch (IOException e) {
                new MojoExecutionException(String.format("Could not store response headers from '%s' to property file", targetUrl), e);
            }
        }
    }

    private byte[] readResponseData(InputStream is) throws MojoExecutionException {
        try {
            final int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((bytesRead = is.read(buffer, 0, bufferSize)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            return os.toByteArray();
        } catch (IOException ioe) {
            throw new MojoExecutionException("Could not read response to file " + responseOutput.getAbsolutePath(), ioe);
        }
    }

    private void detectCharset(byte[] data) {

        nsDetector det = new nsDetector(nsPSMDetector.ALL);
        det.Init(new nsICharsetDetectionObserver() {

            public void Notify(String charset) {
                getLog().info(String.format("Response charset detected '%s'", charset));
                RestClientMojo.this.responseCharset = charset;
            }
        });

        BufferedInputStream imp = new BufferedInputStream(new ByteArrayInputStream(data));

        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;

        try {
            while ((len = imp.read(buf, 0, buf.length)) != -1) {

                // Check if the stream is only ascii.
                if (isAscii)
                    isAscii = det.isAscii(buf, len);

                // DoIt if non-ascii and not done yet.
                if (!isAscii && !done)
                    done = det.DoIt(buf, len, false);
            }
        } catch (IOException ioe) {
            // Do nothing
        }
        det.DataEnd();
    }

    private void saveResponse(byte[] data) throws MojoExecutionException {
        if (responseOutput != null) {
            try {
                responseOutput.getParentFile().mkdirs();
                OutputStream os = new FileOutputStream(responseOutput);
                os.write(data);
                os.flush();
                os.close();
            } catch (FileNotFoundException e) {
                throw new MojoExecutionException("Could not open file " + responseOutput.getAbsolutePath(), e);
            } catch (IOException e) {
                throw new MojoExecutionException("Could not write response to file " + responseOutput.getAbsolutePath(), e);
            }
        }
    }

    private String basicAuthorization(String username, String password) {
        String usernamePasswd = username + ":" + password;
        return "Basic " + Base64.encodeBase64String(usernamePasswd.getBytes());
    }

    private void installSslSkipVerification() throws MojoExecutionException {
        try {
            X509TrustManager trustManager = createHollowTrustManager();
            TrustManager[] trustManagers = new TrustManager[]{trustManager};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            throw new MojoExecutionException("Could not skip ssl validations", e);
        }
    }

    private X509TrustManager createHollowTrustManager() {
        return new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }
        };
    }

    private SSLSocketFactory createSSLSocketFactory() throws MojoExecutionException {
        KeyStore keyStore = null;
        try {
            InputStream keyStoreInputStream = new FileInputStream(sslConfig.getKeystore());
            keyStore = KeyStore.getInstance(sslConfig.getType());
            keyStore.load(keyStoreInputStream, sslConfig.getKeystorePasswordAsCharArray());
        } catch (Exception e) {
            throw new MojoExecutionException("Could initialize keystore", e);
        }
        KeyManager[] keyManagers = initializeKeyManagers(keyStore);
        try {
            InputStream trustStoreInputStream = new FileInputStream(sslConfig.getTruststore());
            KeyStore trustStore = KeyStore.getInstance(sslConfig.getType());
            trustStore.load(trustStoreInputStream, sslConfig.getTrustStorePasswordAsCharArray());
            String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance(sslConfig.getProtocol());
            sslContext.init(keyManagers, trustManagers, SecureRandom.getInstance("SHA1PRNG"));
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new MojoExecutionException("Could not initialize truststore", e);
        }

    }

    private KeyManager[] initializeKeyManagers(KeyStore keyStore) throws MojoExecutionException {
        if (sslConfig.getClientAlias() != null) {
            return new KeyManager[]{new KeyManagerImpl(sslConfig, keyStore)};
        } else {
            try {
                String defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(defaultAlgorithm);
                keyManagerFactory.init(keyStore, sslConfig.getKeystorePasswordAsCharArray());
                return keyManagerFactory.getKeyManagers();
            } catch (Exception e) {
                throw new MojoExecutionException("Could not initialize KeyManagers", e);
            }
        }
    }
}
