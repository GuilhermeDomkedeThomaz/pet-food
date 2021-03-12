package br.com.fatec.petfood.model.dto;

import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.model.generic.ProductRequest;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class RequestReturnDTO {

    @NonNull
    private String sellerName;

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

    @NonNull
    private Double totalValue;

    @NonNull
    private Status status;

    private Integer rate;

    @NonNull
    private String defaultDateTime;

    private String lastUpdateDateTime;
}
