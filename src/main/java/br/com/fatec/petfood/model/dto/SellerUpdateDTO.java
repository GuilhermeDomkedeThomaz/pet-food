package br.com.fatec.petfood.model.dto;

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
public class SellerUpdateDTO extends LoginDTO {

    private String name;

    private String cellPhone;

    private String address;

    private Integer numberAddress;

    private String cep;

    private String city;

    private String uf;
}
