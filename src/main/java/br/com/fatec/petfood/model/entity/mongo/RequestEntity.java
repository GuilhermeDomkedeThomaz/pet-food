package br.com.fatec.petfood.model.entity.mongo;

import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.enums.Status;
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
import java.util.List;

@Data
@RequiredArgsConstructor
@Document(collection = "request")
public class RequestEntity implements Serializable {

    @Id
    @JsonIgnore
    private ObjectId id;

    @NonNull
    private ObjectId sellerId;

    @NonNull
    private String sellerName;

    @NonNull
    private ObjectId userId;

    @NonNull
    private String userName;

    @NonNull
    private List<ProductRequest> products;

    @NonNull
    private Double totalPricePromotion;

    @NonNull
    private Double totalPrice;

    @NonNull
    private Integer totalQuantity;

    @NonNull
    private Double shippingPrice;

    private Double totalValue;

    @NonNull
    private Status status;

    private Integer rate;

    @CreatedDate
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private DateTime defaultDateTime;

    private DateTime lastUpdateDateTime;
}
