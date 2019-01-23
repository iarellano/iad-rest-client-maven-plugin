package com.github.iarellano.rest_client.configuration;

import java.io.File;

public class MultipartInput extends FormInput {

    private File file;

    private String contentType;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        if (file == null) {
            return super.toString();
        } else {
            return "MultipartInput{" +
                    "file=" + file +
                    '}';
        }
    }
}
