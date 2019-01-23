package com.github.iarellano.rest_client;


import com.github.iarellano.rest_client.configuration.CookieConfig;
import com.github.iarellano.rest_client.support.app.Application;
import com.github.iarellano.rest_client.support.net.ProxyServer;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.Assert.*;

public class RestClientMojoTest extends MojoTest {

    private static ConfigurableApplicationContext context;

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {

        }

        @Override
        protected void after() {

        }
    };

    @BeforeClass
    public static void beforeClassHook() {
        System.setProperty("spring.profiles.active", "[default]");
        context = SpringApplication.run(Application.class, new String[0]);
    }

    @AfterClass
    public static void afterClassHook() {
        SpringApplication.exit(context, new ExitCodeGenerator() {

            @Override
            public int getExitCode() {
                return 0;
            }
        });
    }

    @Test
    public void testFormSubmit()
            throws Exception {
        File pom = new File("target/test/projects-to-test/form-submit");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.entity");
        assertEquals(value, "John Connor");
    }

    @Test
    public void testFollowRedirect()
            throws Exception {
        File pom = new File("target/test/projects-to-test/follow-redirect");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.content");
        assertEquals(value, "You have been redirected here!");
    }

    @Test
    public void testGet()
            throws Exception {
        File pom = new File("target/test/projects-to-test/get");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.content");
        assertEquals(value, "Hello, World");
    }

    @Test
    public void testBasicAuthentication()
            throws Exception {
        File pom = new File("target/test/projects-to-test/get-basic-authentication");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.content");
        assertEquals(value, "Hi john-connor");

    }

    @Test
    public void testBasicAuthentication401()
            throws Exception {
        File pom = new File("target/test/projects-to-test/get-basic-authentication-401");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.content");
        assertEquals(value, "I do not know any \"sarah-connor\"");

        File headerFile = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_HEADER_VARIABLE);
        assertTrue(headerFile.exists());
    }

    @Test
    public void testGetPathVariable()
            throws Exception {
        File pom = new File("target/test/projects-to-test/get-path-variable");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.content");
        assertEquals(value, "Hello, Skynet");
    }

    @Test
    public void testHeaders()
            throws Exception {
        File pom = new File("target/test/projects-to-test/headers");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = ((JSONArray) JsonPath.read(document, "$.[?(@.headerName == 'Language')].headerValues[0]")).get(0).toString();
        assertEquals(value, "Mexican Spanish");
    }

    @Test
    public void testInlinePayload()
            throws Exception {
        File pom = new File("target/test/projects-to-test/inline-payload");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String content = FileUtils.readFileToString(output);
        assertEquals(content, "OK");
    }

    @Test
    public void testMultipartSubmit()
            throws Exception {
        File pom = new File("target/test/projects-to-test/multipart-submit");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        assertEquals("file1.txt", JsonPath.read(document, "$.[0].entity"));
        assertEquals("file2.txt", JsonPath.read(document, "$.[1].entity"));
    }

    @Test
    public void testProxy()
            throws Exception {

        Properties properties = new Properties();
        properties.load(new FileInputStream("target/test/projects-to-test/proxy/proxy.properties"));
        Integer port = Integer.valueOf(properties.getProperty("test.proxy.server.port"));
        ProxyServer proxyServer = new ProxyServer(port);
        proxyServer.start();
        File pom = new File("target/test/projects-to-test/proxy");
        assertNotNull(pom);
        assertTrue(pom.exists());
        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();
        proxyServer.stop();
        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.content");
        assertEquals(value, "Hello, World");
    }

    @Test
    public void testQueryString()
            throws Exception {
        File pom = new File("target/test/projects-to-test/query-string");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        String value = JsonPath.read(document, "$.content");
        assertEquals(value, "Hello, John Connor");
    }

    @Test
    public void testRawFileSubmit() throws Exception {
        File pom = new File("target/test/projects-to-test/raw-file-submit");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);
        String content = FileUtils.readFileToString(output);
        assertEquals(content, "OK");
    }

    @Test
    public void testResponseHeadersAsProperties() throws Exception {
        File pom = new File("target/test/projects-to-test/response-headers-as-properties");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();


        File output = (File) rule.getVariableValueFromObject(restClientMojo, OUTPUT_VARIABLE);


        String prefix = (String) rule.getVariableValueFromObject(restClientMojo, RESPONSE_HEADERS_PREFIX);
        String contentType = System.getProperty(prefix + "Content-Type");
        assertEquals(contentType, "application/json;charset=utf-8");
    }

    @Test
    public void testSetCookie()
            throws Exception {
        File pom = new File("target/test/projects-to-test/set-cookie");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        File output = ((CookieConfig) rule.getVariableValueFromObject(restClientMojo, COOKIE_JAR)).getCookieJar();
        String json = FileUtils.readFileToString(output);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        JSONArray result = JsonPath.read(document, "$.*.[?(@.name == 'uuid')].domain");
        String value = result.get(0).toString();
        assertEquals(value, "localhost.local");
    }

    @Test
    public void testXpathExtraction() throws Exception {

        // /test:parent/child:innerChild/grandChild/text()
        File pom = new File("target/test/projects-to-test/xpath-extraction");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        String prefix = (String) rule.getVariableValueFromObject(restClientMojo, EXTRACT_PREFIX);
        String contentType = System.getProperty(prefix + "nodeValue");
        assertEquals(contentType, "I am grandson of my parent's parent");
    }

    @Test
    public void testJsonExtraction() throws Exception {

        // /test:parent/child:innerChild/grandChild/text()
        File pom = new File("target/test/projects-to-test/json-extraction");
        assertNotNull(pom);
        assertTrue(pom.exists());

        RestClientMojo restClientMojo = (RestClientMojo) rule.lookupConfiguredMojo(pom, "http-request");
        assertNotNull(restClientMojo);
        restClientMojo.execute();

        String prefix = (String) rule.getVariableValueFromObject(restClientMojo, EXTRACT_PREFIX);
        String propertyValue = System.getProperty(prefix + "content");
        System.out.println(propertyValue);
        assertEquals(propertyValue, "Hello, World");
    }
}

