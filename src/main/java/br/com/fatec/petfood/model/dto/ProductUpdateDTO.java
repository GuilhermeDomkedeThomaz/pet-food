package br.com.fatec.petfood.model.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProductUpdateDTO {

    @NonNull
    private String description;

    @NonNull
    private String brand;

    @NonNull
    private Double pricePromotion;

    @NonNull
    private Double price;

    @NonNull
    private Integer stock;

    @NonNull
    private String imageUrl;

    private String additionalInfo;
}
