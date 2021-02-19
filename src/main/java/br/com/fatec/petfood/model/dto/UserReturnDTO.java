package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReturnDTO {

    private String name;

    private String email;

    private String document;

    private String cellPhone;

    private Date birthdayDate;

    private String address;

    private String cep;

    private String city;

    private CityZone cityZone;

    private Pets pets;

    private String defaultDateTime;
}
