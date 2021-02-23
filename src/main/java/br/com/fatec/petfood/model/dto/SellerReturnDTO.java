package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerReturnDTO {

    private String name;

    private String email;

    private RegistrationInfos registrationInfos;

    private CityZone cityZone;

    private String defaultDateTime;
}
