package com.github.iarellano.rest_client;

import com.github.iarellano.rest_client.support.app.Application;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
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

import static org.junit.Assert.*;

public class MutualSSLRestClientMojoTest extends MojoTest {

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
        System.setProperty("spring.profiles.active", "2-way-tls");
        System.setProperty("javax.net.debug", "all");
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
    public void testMutualSsl()
            throws Exception {
        File pom = new File("target/test/projects-to-test/mutual-ssl");
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
}
