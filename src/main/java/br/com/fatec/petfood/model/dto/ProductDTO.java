package br.com.fatec.petfood.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ProductDTO {

    @NonNull
    private String sellerName;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String brand;

    @NonNull
    private String category;

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
