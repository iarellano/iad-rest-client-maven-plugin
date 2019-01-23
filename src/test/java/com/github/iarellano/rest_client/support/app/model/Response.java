package com.github.iarellano.rest_client.support.app.model;

import java.util.Date;

public class Response {

    private String message;

    private String entity;

    private Date timestamp = new Date();

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, String entity) {
        this.message = message;
        this.entity = entity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
