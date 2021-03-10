package br.com.fatec.petfood.integration.model.mapper;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.RequestMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RequestMapperTest extends IntegrationTest {

    @Autowired
    private RequestMapper requestMapper;

    @Test
    public void shouldMapperToEntity() {
        SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
        UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
        ProductRequest firstProductRequest = EnhancedRandom.random(ProductRequest.class);
        ProductRequest secondProductRequest = EnhancedRandom.random(ProductRequest.class);
        firstProductRequest.setQuantity(5);
        firstProductRequest.setPrice(9.99);
        firstProductRequest.setPricePromotion(9.99);
        secondProductRequest.setQuantity(5);
        secondProductRequest.setPrice(9.99);
        secondProductRequest.setPricePromotion(9.99);
        List<ProductRequest> productRequestList = List.of(firstProductRequest, secondProductRequest);

        RequestEntity requestEntity = requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(),
                userEntity.getName(), productRequestList, 5.99, Status.CREATED);

        Assertions.assertEquals(requestEntity.getSellerId(), sellerEntity.getId());
        Assertions.assertEquals(requestEntity.getSellerName(), sellerEntity.getName());
        Assertions.assertEquals(requestEntity.getUserId(), userEntity.getId());
        Assertions.assertEquals(requestEntity.getUserName(), userEntity.getName());
        Assertions.assertEquals(requestEntity.getProducts(), productRequestList);
        Assertions.assertEquals(requestEntity.getTotalPricePromotion(),
                ((firstProductRequest.getPricePromotion() * firstProductRequest.getQuantity()) +
                        (secondProductRequest.getPricePromotion() * secondProductRequest.getQuantity())));
        Assertions.assertEquals(requestEntity.getTotalPrice(),
                ((firstProductRequest.getPrice() * firstProductRequest.getQuantity()) +
                        (secondProductRequest.getPrice() * secondProductRequest.getQuantity())));
        Assertions.assertEquals(requestEntity.getTotalQuantity(),
                (firstProductRequest.getQuantity() + secondProductRequest.getQuantity()));
        Assertions.assertEquals(requestEntity.getShippingPrice(), 5.99);
        Assertions.assertEquals(requestEntity.getTotalValue(),
                (requestEntity.getTotalPrice() + requestEntity.getShippingPrice()));
        Assertions.assertEquals(requestEntity.getStatus(), Status.CREATED);
    }
}
