package com.github.iarellano.rest_client.configuration;

/**
 * Created by isaiasarellano on 1/13/19.
 */
public class BasicCredentials {

    private String username = "";

    private String password = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "BasicCredentials{" +
                "username='" + username + '\'' +
                ", password='*********'" +
                '}';
    }
}
