package br.com.fatec.petfood.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    private String sellerName;

    private String userName;

    private List<ProductRequestDTO> products;

    private Double shippingPrice;
}
