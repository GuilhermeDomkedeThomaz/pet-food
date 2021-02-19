package br.com.fatec.petfood.integration.utils;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.utils.ValidateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidateUtilsTest extends IntegrationTest {

    @Autowired
    private ValidateUtils validateUtils;

    @Test
    public void shouldVerifyIsNull() {
        Boolean verify = validateUtils.isNotNullAndNotEmpty(null);

        Assertions.assertFalse(verify);
    }

    @Test
    public void shouldVerifyIsNotNullAndEmpty() {
        Boolean verify = validateUtils.isNotNullAndNotEmpty("");

        Assertions.assertFalse(verify);
    }

    @Test
    public void shouldVerifyIsNotNullAndNotEmpty() {
        Boolean verify = validateUtils.isNotNullAndNotEmpty("1234");

        Assertions.assertTrue(verify);
    }
}
