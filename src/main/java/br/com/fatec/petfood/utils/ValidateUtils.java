package br.com.fatec.petfood.utils;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ValidateUtils {

    public Boolean isNotNullAndNotEmpty(String string) {
        if (Objects.isNull(string))
            return false;
        else
            return !string.isEmpty();
    }
}
