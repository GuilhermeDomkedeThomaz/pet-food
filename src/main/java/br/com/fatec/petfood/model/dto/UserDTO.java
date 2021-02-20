package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.generic.RegistrationInfos;
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

    private RegistrationInfos registrationInfos;

    private Date birthdayDate;
}
