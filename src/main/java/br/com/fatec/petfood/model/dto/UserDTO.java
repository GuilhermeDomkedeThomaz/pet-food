package br.com.fatec.petfood.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends UserLoginDTO {

    private String name;

    private String document;

    private String cellPhone;

    private Date birthdayDate;

    private String address;

    private Integer numberAddress;

    private String cep;

    private String city;
}
