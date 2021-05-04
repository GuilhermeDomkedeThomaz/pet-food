package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerReturnDTO {

    private String name;

    private String email;

    private RegistrationInfos registrationInfos;

    private String imageUrl;

    private String weekInitialTimeOperation;

    private String weekFinalTimeOperation;

    private String weekendInitialTimeOperation;

    private String weekendFinalTimeOperation;

    private CityZone cityZone;

    private List<Category> categories;

    private String defaultDateTime;
}
