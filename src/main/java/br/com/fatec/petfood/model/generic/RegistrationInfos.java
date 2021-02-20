package br.com.fatec.petfood.model.generic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationInfos {

    private String document;

    private String cellPhone;

    private String address;

    private Integer numberAddress;

    private String cep;

    private String city;
}
