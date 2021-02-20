package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
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

    private RegistrationInfos registrationInfos;

    private Date birthdayDate;

    private CityZone cityZone;

    private Pets pets;

    private String defaultDateTime;
}
