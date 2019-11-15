package me.geso.modernfit;

import org.springframework.http.HttpMethod;

public class HttpMethodHolder {
    private final HttpMethod httpMethod;
    private final String path;

    public HttpMethodHolder(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }
}
