package com.github.iarellano.rest_client.support.app.model;

public class Header {

    public String headerName;

    public String[] headerValues;

    public Header(String headerName, String[] headerValues) {
        this.headerName = headerName;
        this.headerValues = headerValues;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String[] getHeaderValues() {
        return headerValues;
    }

    public void setHeaderValues(String[] headerValues) {
        this.headerValues = headerValues;
    }
}
