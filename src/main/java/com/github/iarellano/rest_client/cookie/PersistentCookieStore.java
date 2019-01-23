package com.github.iarellano.rest_client.cookie;

import com.github.iarellano.rest_client.configuration.CookieConfig;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.net.*;
import java.util.List;

public class PersistentCookieStore implements CookieStore, Runnable {

    private final CookieStore cookieStore;

    private final CookieManager cookieManager;

    private final Log log;
    private final CookieConfig cookieConfig;

    public PersistentCookieStore(CookieConfig cookieConfig, Log log) throws IOException {

        this.cookieConfig = cookieConfig;
        this.log = log;

        cookieManager = new CookieManager();
        cookieStore = cookieManager.getCookieStore();

        if (cookieConfig.getCookieJar() != null) {
            loadPersistedCookies();
        }
    }

    private void loadPersistedCookies() throws IOException {
        if (cookieConfig.isRemoveAll()) {
            cookieStore.removeAll();
        } else {
            if (cookieConfig.getCookieJar().isFile()) {
                if (!cookieConfig.getCookieJar().exists()) {
                    cookieConfig.getCookieJar().createNewFile();
                } else {
                    Reader reader = new FileReader(cookieConfig.getCookieJar());
                    JsonObject jsonCookies = new JsonParser().parse(reader).getAsJsonObject();
                    for (String uri : jsonCookies.keySet()) {
                        JsonArray cookieEntries = jsonCookies.getAsJsonArray(uri);
                        for (int i = 0, size = cookieEntries.size(); i < size; i++) {
                            JsonObject jsonCookie = (JsonObject) cookieEntries.get(i);
                            String name = jsonCookie.get("name").getAsString();
                            String value = jsonCookie.get("value").getAsString();
                            HttpCookie cookie = new HttpCookie(name, value);
                            cookie.setPath(jsonCookie.get("path").getAsString());
                            cookie.setVersion(jsonCookie.get("version").getAsInt());
                            cookie.setDomain(jsonCookie.get("domain").getAsString());
                            cookie.setDiscard(jsonCookie.get("discard").getAsBoolean());
                            cookie.setMaxAge(jsonCookie.get("maxEge").getAsLong());
                            cookie.setSecure(jsonCookie.get("secure").getAsBoolean());
                            cookie.setHttpOnly(jsonCookie.get("httpOnly").getAsBoolean());
                            if (jsonCookie.has("comment")) {
                                cookie.setComment(jsonCookie.get("comment").getAsString());
                            }
                            if (jsonCookie.has("commentUrl")) {
                                cookie.setCommentURL(jsonCookie.get("commentUrl").getAsString());
                            }
                            if (jsonCookie.has("portList")) {
                                cookie.setPortlist(jsonCookie.get("portList").getAsString());
                            }
                            try {
                                cookieStore.add(new URI(uri), cookie);
                            } catch (URISyntaxException e) {
                                // Should never happen
                            }
                        }
                    }
                }
            }
        }
    }

    public void persistCookies() throws IOException {
        JsonObject jsonObject = new JsonObject();
        for (URI uri : cookieStore.getURIs()) {
            JsonArray cookies = new JsonArray();
            for (HttpCookie cookie : cookieStore.get(uri)) {
                JsonObject jsonCookie = new JsonObject();
                jsonCookie.addProperty("name", cookie.getName());
                jsonCookie.addProperty("path", cookie.getPath());
                jsonCookie.addProperty("version", cookie.getVersion());
                jsonCookie.addProperty("value", cookie.getValue());
                jsonCookie.addProperty("comment", cookie.getComment());
                jsonCookie.addProperty("commentUrl", cookie.getCommentURL());
                jsonCookie.addProperty("domain", cookie.getDomain());
                jsonCookie.addProperty("discard", cookie.getDiscard());
                jsonCookie.addProperty("maxEge", cookie.getMaxAge());
                jsonCookie.addProperty("portList", cookie.getPortlist());
                jsonCookie.addProperty("secure", cookie.getSecure());
                jsonCookie.addProperty("httpOnly", cookie.isHttpOnly());
                cookies.add(jsonCookie);
            }
            jsonObject.add(uri.toString(), cookies);
        }
        Writer writer = new FileWriter(cookieConfig.getCookieJar(), false);
        new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, writer);
        writer.close();
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    @Override
    public void run() {

    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        cookieStore.add(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return cookieStore.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return cookieStore.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return cookieStore.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return cookieStore.remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return cookieStore.removeAll();
    }
}
