package br.com.fatec.petfood.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ResponseHeadersUtils {

    public HttpHeaders getDefaultResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        return responseHeaders;
    }
}
