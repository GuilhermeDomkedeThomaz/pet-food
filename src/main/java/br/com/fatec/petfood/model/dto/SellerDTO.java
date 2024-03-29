package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.generic.RegistrationInfos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SellerDTO extends LoginDTO {

    private String name;

    private RegistrationInfos registrationInfos;

    private String imageUrl;

    private String weekInitialTimeOperation;

    private String weekFinalTimeOperation;

    private String weekendInitialTimeOperation;

    private String weekendFinalTimeOperation;
}
