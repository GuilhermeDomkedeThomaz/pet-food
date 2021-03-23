package br.com.fatec.petfood.integration.model.mapper;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductMapperTest extends IntegrationTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void shouldMapperToEntity() {
        ProductDTO productDTO = EnhancedRandom.random(ProductDTO.class);
        SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);

        ProductEntity productEntity = productMapper.toEntity(productDTO, sellerEntity.getId(), sellerEntity.getName(), Category.FOOD);

        Assertions.assertEquals(sellerEntity.getId(), productEntity.getSellerId());
        Assertions.assertEquals(sellerEntity.getName(), productEntity.getSellerName());
        Assertions.assertEquals(productDTO.getTitle(), productEntity.getTitle());
        Assertions.assertEquals(productDTO.getDescription(), productEntity.getDescription());
        Assertions.assertEquals(productDTO.getBrand(), productEntity.getBrand());
        Assertions.assertEquals(Category.FOOD, productEntity.getCategory());
        Assertions.assertEquals(productDTO.getPricePromotion(), productEntity.getPricePromotion());
        Assertions.assertEquals(productDTO.getPrice(), productEntity.getPrice());
        Assertions.assertEquals(productDTO.getStock(), productEntity.getStock());
        Assertions.assertEquals(productDTO.getImageUrl(), productEntity.getImageUrl());
        Assertions.assertEquals(productDTO.getAdditionalInfo(), productEntity.getAdditionalInfo());
    }

    @Test
    public void shouldMapperToEntityUpdate() {
        ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);
        ProductUpdateDTO productUpdateDTO = EnhancedRandom.random(ProductUpdateDTO.class);

        ProductEntity productEntityUpdate = productMapper.toEntity(productUpdateDTO, productEntity, Category.FOOD);

        Assertions.assertEquals(productEntity.getId(), productEntityUpdate.getId());
        Assertions.assertEquals(productEntity.getSellerId(), productEntityUpdate.getSellerId());
        Assertions.assertEquals(productEntity.getSellerName(), productEntityUpdate.getSellerName());
        Assertions.assertEquals(productEntity.getTitle(), productEntityUpdate.getTitle());
        Assertions.assertEquals(productUpdateDTO.getDescription(), productEntityUpdate.getDescription());
        Assertions.assertEquals(productUpdateDTO.getBrand(), productEntityUpdate.getBrand());
        Assertions.assertEquals(Category.FOOD, productEntityUpdate.getCategory());
        Assertions.assertEquals(productUpdateDTO.getPricePromotion(), productEntityUpdate.getPricePromotion());
        Assertions.assertEquals(productUpdateDTO.getPrice(), productEntityUpdate.getPrice());
        Assertions.assertEquals(productUpdateDTO.getStock(), productEntityUpdate.getStock());
        Assertions.assertEquals(productUpdateDTO.getImageUrl(), productEntityUpdate.getImageUrl());
        Assertions.assertEquals(productUpdateDTO.getAdditionalInfo(), productEntityUpdate.getAdditionalInfo());
    }

    @Test
    public void shouldMapperToEntityUpdateStock() {
        ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);
        productEntity.setStock(50);

        ProductEntity productEntityUpdate = productMapper.toEntity(productEntity, 10);

        Assertions.assertEquals(productEntity.getId(), productEntityUpdate.getId());
        Assertions.assertEquals(productEntity.getSellerId(), productEntityUpdate.getSellerId());
        Assertions.assertEquals(productEntity.getSellerName(), productEntityUpdate.getSellerName());
        Assertions.assertEquals(productEntity.getTitle(), productEntityUpdate.getTitle());
        Assertions.assertEquals(productEntity.getDescription(), productEntityUpdate.getDescription());
        Assertions.assertEquals(productEntity.getBrand(), productEntityUpdate.getBrand());
        Assertions.assertEquals(productEntity.getCategory(), productEntityUpdate.getCategory());
        Assertions.assertEquals(productEntity.getPricePromotion(), productEntityUpdate.getPricePromotion());
        Assertions.assertEquals(productEntity.getPrice(), productEntityUpdate.getPrice());
        Assertions.assertEquals(productEntityUpdate.getStock(), 10);
        Assertions.assertEquals(productEntity.getImageUrl(), productEntityUpdate.getImageUrl());
        Assertions.assertEquals(productEntity.getAdditionalInfo(), productEntityUpdate.getAdditionalInfo());
    }

    @Test
    public void shouldMapperToReturnDTO() {
        ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);

        ProductReturnDTO productReturnDTO = productMapper.toReturnDTO(productEntity);

        Assertions.assertEquals(productEntity.getSellerId().toString(), productReturnDTO.getSellerId());
        Assertions.assertEquals(productEntity.getSellerName(), productReturnDTO.getSellerName());
        Assertions.assertEquals(productEntity.getTitle(), productReturnDTO.getTitle());
        Assertions.assertEquals(productEntity.getDescription(), productReturnDTO.getDescription());
        Assertions.assertEquals(productEntity.getBrand(), productReturnDTO.getBrand());
        Assertions.assertEquals(productEntity.getCategory(), productReturnDTO.getCategory());
        Assertions.assertEquals(productEntity.getPricePromotion(), productReturnDTO.getPricePromotion());
        Assertions.assertEquals(productEntity.getPrice(), productReturnDTO.getPrice());
        Assertions.assertEquals(productEntity.getStock(), productReturnDTO.getStock());
        Assertions.assertEquals(productEntity.getImageUrl(), productReturnDTO.getImageUrl());
        Assertions.assertEquals(productEntity.getAdditionalInfo(), productReturnDTO.getAdditionalInfo());
    }

    @Test
    public void shouldMapperToProductRequest() {
        ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);

        ProductRequest productRequest = productMapper.toProductRequest(productEntity, 1);

        Assertions.assertEquals(productRequest.getProductId(), productEntity.getId());
        Assertions.assertEquals(productRequest.getTitle(), productEntity.getTitle());
        Assertions.assertEquals(productRequest.getPricePromotion(), productEntity.getPricePromotion());
        Assertions.assertEquals(productRequest.getPrice(), productEntity.getPrice());
        Assertions.assertEquals(productRequest.getQuantity(), 1);
    }
}
