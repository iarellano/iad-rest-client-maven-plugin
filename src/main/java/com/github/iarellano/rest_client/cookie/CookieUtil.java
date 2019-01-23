package com.github.iarellano.rest_client.cookie;

import com.github.iarellano.rest_client.configuration.CookieConfig;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

public class CookieUtil {

    private final CookieConfig cookieConfig;

    private final CookieManager origCookieManager;

    private final Log log;

    private PersistentCookieStore cookieStore;

    public CookieUtil(CookieConfig cookieConfig, Log log) {
        this.cookieConfig = cookieConfig;
        this.log = log;
        origCookieManager = (CookieManager) CookieHandler.getDefault();
    }

    public void installCookieManager() throws IOException {
        cookieStore = new PersistentCookieStore(cookieConfig, log);
        CookieManager cm = cookieStore.getCookieManager();
        cm.setCookiePolicy(new ConfigurableCookiePolicy(cookieConfig, log));
        CookieHandler.setDefault(cm);
    }

    public void persistCookies() throws IOException {
        cookieStore.persistCookies();
    }

    public void restoreOriginalCookieManager() {
        CookieHandler.setDefault(origCookieManager);
    }
}
