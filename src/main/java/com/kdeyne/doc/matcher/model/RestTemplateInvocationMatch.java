package com.kdeyne.doc.matcher.model;

import com.kdeyne.doc.model.HTTPMethod;

public class RestTemplateInvocationMatch implements InvocationMatch {

    private String url;
    private HTTPMethod httpMethod;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HTTPMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public void printResult() {
        System.out.println("- URL: " + getUrl());
        System.out.println("- HTTP method: " + getHttpMethod());
        System.out.println();
    }
}
