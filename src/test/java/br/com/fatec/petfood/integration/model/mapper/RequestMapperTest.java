package br.com.fatec.petfood.integration.model.mapper;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.RequestReturnDTO;
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
import java.util.Objects;

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

    @Test
    public void shouldMapperToEntityUpdate() {
        RequestEntity requestEntity = EnhancedRandom.random(RequestEntity.class);
        ProductRequest firstProductRequest = EnhancedRandom.random(ProductRequest.class);
        ProductRequest secondProductRequest = EnhancedRandom.random(ProductRequest.class);
        requestEntity.setStatus(Status.CREATED);
        requestEntity.setShippingPrice(5.99);
        firstProductRequest.setQuantity(5);
        firstProductRequest.setPrice(9.99);
        firstProductRequest.setPricePromotion(9.99);
        secondProductRequest.setQuantity(5);
        secondProductRequest.setPrice(9.99);
        secondProductRequest.setPricePromotion(9.99);
        List<ProductRequest> productRequestList = List.of(firstProductRequest, secondProductRequest);

        RequestEntity requestUpdateEntity = requestMapper.toEntity(requestEntity, productRequestList, 0.00, Status.PROCESSED);

        Assertions.assertEquals(requestEntity.getId(), requestUpdateEntity.getId());
        Assertions.assertEquals(requestEntity.getSellerId(), requestUpdateEntity.getSellerId());
        Assertions.assertEquals(requestEntity.getSellerName(), requestUpdateEntity.getSellerName());
        Assertions.assertEquals(requestEntity.getUserId(), requestUpdateEntity.getUserId());
        Assertions.assertEquals(requestEntity.getUserName(), requestUpdateEntity.getUserName());
        Assertions.assertEquals(requestUpdateEntity.getProducts(), productRequestList);
        Assertions.assertEquals(requestUpdateEntity.getTotalPricePromotion(),
                ((firstProductRequest.getPricePromotion() * firstProductRequest.getQuantity()) +
                        (secondProductRequest.getPricePromotion() * secondProductRequest.getQuantity())));
        Assertions.assertEquals(requestUpdateEntity.getTotalPrice(),
                ((firstProductRequest.getPrice() * firstProductRequest.getQuantity()) +
                        (secondProductRequest.getPrice() * secondProductRequest.getQuantity())));
        Assertions.assertEquals(requestUpdateEntity.getTotalQuantity(),
                (firstProductRequest.getQuantity() + secondProductRequest.getQuantity()));
        Assertions.assertEquals(requestUpdateEntity.getShippingPrice(), 0.00);
        Assertions.assertEquals(requestUpdateEntity.getTotalValue(),
                (requestUpdateEntity.getTotalPrice() + requestUpdateEntity.getShippingPrice()));
        Assertions.assertEquals(requestUpdateEntity.getStatus(), Status.PROCESSED);
        Assertions.assertEquals(requestEntity.getRate(), requestUpdateEntity.getRate());
        Assertions.assertEquals(requestEntity.getDefaultDateTime(), requestUpdateEntity.getDefaultDateTime());
        Assertions.assertNotNull(requestUpdateEntity.getLastUpdateDateTime());
    }

    @Test
    public void shouldMapperToEntityUpdateStatus() {
        RequestEntity requestEntity = EnhancedRandom.random(RequestEntity.class);
        requestEntity.getProducts().forEach(product -> {
            product.setPrice(9.99);
            product.setPricePromotion(6.99);
            product.setQuantity(5);
        });
        requestEntity.setShippingPrice(9.99);
        requestEntity.setStatus(Status.CREATED);

        RequestEntity requestUpdateEntity = requestMapper.toEntity(requestEntity, Status.PROCESSED);

        Assertions.assertEquals(requestEntity.getId(), requestUpdateEntity.getId());
        Assertions.assertEquals(requestEntity.getSellerId(), requestUpdateEntity.getSellerId());
        Assertions.assertEquals(requestEntity.getSellerName(), requestUpdateEntity.getSellerName());
        Assertions.assertEquals(requestEntity.getUserId(), requestUpdateEntity.getUserId());
        Assertions.assertEquals(requestEntity.getUserName(), requestUpdateEntity.getUserName());
        Assertions.assertEquals(requestEntity.getProducts(), requestUpdateEntity.getProducts());
        Assertions.assertEquals(requestEntity.getTotalPricePromotion(), requestUpdateEntity.getTotalPricePromotion());
        Assertions.assertEquals(requestEntity.getTotalPrice(), requestUpdateEntity.getTotalPrice());
        Assertions.assertEquals(requestEntity.getTotalQuantity(), requestUpdateEntity.getTotalQuantity());
        Assertions.assertEquals(requestEntity.getShippingPrice(), requestUpdateEntity.getShippingPrice());
        Assertions.assertEquals(requestUpdateEntity.getTotalValue(),
                (requestUpdateEntity.getTotalPrice() + requestUpdateEntity.getShippingPrice()));
        Assertions.assertEquals(requestUpdateEntity.getStatus(), Status.PROCESSED);
        Assertions.assertEquals(requestEntity.getRate(), requestUpdateEntity.getRate());
        Assertions.assertEquals(requestEntity.getDefaultDateTime(), requestUpdateEntity.getDefaultDateTime());
        Assertions.assertNotNull(requestUpdateEntity.getLastUpdateDateTime());
    }

    @Test
    public void shouldMapperToEntityUpdateRate() {
        RequestEntity requestEntity = EnhancedRandom.random(RequestEntity.class);
        requestEntity.getProducts().forEach(product -> {
            product.setPrice(9.99);
            product.setPricePromotion(6.99);
            product.setQuantity(5);
        });
        requestEntity.setRate(null);
        requestEntity.setShippingPrice(9.99);

        RequestEntity requestUpdateEntity = requestMapper.toEntity(requestEntity, 10);

        Assertions.assertEquals(requestEntity.getId(), requestUpdateEntity.getId());
        Assertions.assertEquals(requestEntity.getSellerId(), requestUpdateEntity.getSellerId());
        Assertions.assertEquals(requestEntity.getSellerName(), requestUpdateEntity.getSellerName());
        Assertions.assertEquals(requestEntity.getUserId(), requestUpdateEntity.getUserId());
        Assertions.assertEquals(requestEntity.getUserName(), requestUpdateEntity.getUserName());
        Assertions.assertEquals(requestEntity.getProducts(), requestUpdateEntity.getProducts());
        Assertions.assertEquals(requestEntity.getTotalPricePromotion(), requestUpdateEntity.getTotalPricePromotion());
        Assertions.assertEquals(requestEntity.getTotalPrice(), requestUpdateEntity.getTotalPrice());
        Assertions.assertEquals(requestEntity.getTotalQuantity(), requestUpdateEntity.getTotalQuantity());
        Assertions.assertEquals(requestEntity.getShippingPrice(), requestUpdateEntity.getShippingPrice());
        Assertions.assertEquals(requestUpdateEntity.getTotalValue(),
                (requestUpdateEntity.getTotalPrice() + requestUpdateEntity.getShippingPrice()));
        Assertions.assertEquals(requestEntity.getStatus(), requestUpdateEntity.getStatus());
        Assertions.assertEquals(requestUpdateEntity.getRate(), 10);
        Assertions.assertEquals(requestEntity.getDefaultDateTime(), requestUpdateEntity.getDefaultDateTime());
        Assertions.assertNotNull(requestUpdateEntity.getLastUpdateDateTime());
    }

    @Test
    public void shouldMapperToReturnDTO() {
        RequestEntity requestEntity = EnhancedRandom.random(RequestEntity.class);

        RequestReturnDTO requestReturnDTO = requestMapper.toReturnDTO(requestEntity);

        Assertions.assertEquals(requestReturnDTO.getId(), requestEntity.getId().toString());
        Assertions.assertEquals(requestReturnDTO.getSellerName(), requestEntity.getSellerName());
        Assertions.assertEquals(requestReturnDTO.getUserName(), requestEntity.getUserName());
        Assertions.assertEquals(requestReturnDTO.getProducts(), requestEntity.getProducts());
        Assertions.assertEquals(requestReturnDTO.getTotalPricePromotion(), requestEntity.getTotalPricePromotion());
        Assertions.assertEquals(requestReturnDTO.getTotalPrice(), requestEntity.getTotalPrice());
        Assertions.assertEquals(requestReturnDTO.getTotalQuantity(), requestEntity.getTotalQuantity());
        Assertions.assertEquals(requestReturnDTO.getShippingPrice(), requestEntity.getShippingPrice());
        Assertions.assertEquals(requestReturnDTO.getTotalValue(), requestEntity.getTotalValue());
        Assertions.assertEquals(requestReturnDTO.getStatus(), requestEntity.getStatus());
        Assertions.assertEquals(requestReturnDTO.getDefaultDateTime(), requestEntity.getDefaultDateTime().toString());

        if (Objects.nonNull(requestEntity.getRate()))
            Assertions.assertEquals(requestReturnDTO.getRate(), requestEntity.getRate());

        if (Objects.nonNull(requestEntity.getLastUpdateDateTime()))
            Assertions.assertEquals(requestReturnDTO.getLastUpdateDateTime(), requestEntity.getLastUpdateDateTime().toString());
        else
            Assertions.assertEquals(requestReturnDTO.getLastUpdateDateTime(), "");
    }
}
