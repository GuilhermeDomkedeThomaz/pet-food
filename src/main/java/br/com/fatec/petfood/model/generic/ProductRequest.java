package br.com.fatec.petfood.model.generic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @JsonIgnore
    private ObjectId productId;

    private String title;

    private Double pricePromotion;

    private Double price;

    private Integer quantity;
}
