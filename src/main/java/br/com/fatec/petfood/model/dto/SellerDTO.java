package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.generic.RegistrationInfos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SellerDTO extends LoginDTO {

    private String name;

    private RegistrationInfos registrationInfos;
}