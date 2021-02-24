package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.enums.Pets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

@Data
@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProductReturnDTO extends ProductDTO {

    @NonNull
    private String sellerId;

    @NonNull
    private Pets pets;

    @NonNull
    private String defaultDateTime;
}
