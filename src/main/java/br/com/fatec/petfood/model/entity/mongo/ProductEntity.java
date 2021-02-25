package br.com.fatec.petfood.model.entity.mongo;

import br.com.fatec.petfood.model.enums.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Document(collection = "product")
public class ProductEntity implements Serializable {

    @Id
    @JsonIgnore
    private ObjectId id;

    @NonNull
    private ObjectId sellerId;

    @NonNull
    private String sellerName;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String brand;

    @NonNull
    private Category category;

    @NonNull
    private Double pricePromotion;

    @NonNull
    private Double price;

    @NonNull
    private Integer stock;

    @NonNull
    private String imageUrl;

    private String additionalInfo;

    @CreatedDate
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private DateTime defaultDateTime;
}
