package br.com.fatec.petfood.integration.utils;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.utils.ResponseHeadersUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class ResponseHeadersUtilsTest extends IntegrationTest {

    @Autowired
    private ResponseHeadersUtils responseHeadersUtils;

    @Test
    public void shouldVerifyResponseHeadersUtilsGetDefaultResponseHeaders() {
        HttpHeaders defaultResponseHeaders = responseHeadersUtils.getDefaultResponseHeaders();

        Assertions.assertEquals(defaultResponseHeaders.getAccessControlAllowOrigin(), "*");
    }
}
