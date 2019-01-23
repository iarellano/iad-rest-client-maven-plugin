package com.github.iarellano.rest_client.cookie;

import com.github.iarellano.rest_client.configuration.CookieConfig;
import org.apache.maven.plugin.logging.Log;

import java.net.*;

public class ConfigurableCookiePolicy implements CookiePolicy {

    private final CookieConfig cookieConfig;

    private final Log log;

    public ConfigurableCookiePolicy(CookieConfig cookieConfig, Log log) {
        this.cookieConfig = cookieConfig;
        this.log = log;
    }

    @Override
    public boolean shouldAccept(URI uri, HttpCookie cookie) {
        try {
            String host = InetAddress.getByName(uri.getHost()).getCanonicalHostName();

            for (String accept : cookieConfig.getAccept()) {
                if (HttpCookie.domainMatches(accept, host)) {
                    log.info(String.format("Cookie policy has accepted cookie '%s' from domain '%s' given cookieConfig.accept entry '%s'", cookie.getName(), host, accept));
                    return true;
                }
            }
            return cookieConfig.getCookiePolicy().shouldAccept(uri, cookie);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}