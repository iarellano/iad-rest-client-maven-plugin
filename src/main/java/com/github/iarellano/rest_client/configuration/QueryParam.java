package com.github.iarellano.rest_client.configuration;

public class QueryParam {

    private static final String EMPTY_VALUE = "";

    private String name;

    private String value = EMPTY_VALUE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "QueryParam{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
