package com.github.iarellano.rest_client.configuration;

import java.io.File;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

public class CookieConfig {

    private CookiePolicy cookiePolicy = CookiePolicy.ACCEPT_ORIGINAL_SERVER;

    private File cookieJar;

    private boolean removeAll = false;

    private List<String> accept = new ArrayList<>();

    public CookiePolicy getCookiePolicy() {
        return cookiePolicy;
    }

    public void setCookiePolicy(CookiePolicy cookiePolicy) {
        this.cookiePolicy = cookiePolicy;
    }

    public File getCookieJar() {
        return cookieJar;
    }

    public void setCookieJar(File cookieJar) {
        this.cookieJar = cookieJar;
    }

    public boolean isRemoveAll() {
        return removeAll;
    }

    public void setRemoveAll(boolean removeAll) {
        this.removeAll = removeAll;
    }

    public List<String> getAccept() {
        return accept;
    }

    public void setAccept(List<String> accept) {
        this.accept = accept;
    }

}
