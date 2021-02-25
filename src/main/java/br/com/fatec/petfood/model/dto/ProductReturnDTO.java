package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.enums.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProductReturnDTO extends ProductDTO {

    @NonNull
    private String sellerId;

    @NonNull
    private Category category;

    @NonNull
    private String defaultDateTime;
}
