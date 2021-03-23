package br.com.fatec.petfood.model.mapper;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.generic.ProductRequest;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mappings({
            @Mapping(target = "sellerId", source = "sellerId"),
            @Mapping(target = "sellerName", source = "sellerName"),
            @Mapping(target = "title", source = "productDTO.title"),
            @Mapping(target = "description", source = "productDTO.description"),
            @Mapping(target = "brand", source = "productDTO.brand"),
            @Mapping(target = "category", source = "category"),
            @Mapping(target = "pricePromotion", source = "productDTO.pricePromotion"),
            @Mapping(target = "price", source = "productDTO.price"),
            @Mapping(target = "stock", source = "productDTO.stock"),
            @Mapping(target = "imageUrl", source = "productDTO.imageUrl"),
            @Mapping(target = "additionalInfo", source = "productDTO.additionalInfo"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    ProductEntity toEntity(ProductDTO productDTO, ObjectId sellerId, String sellerName, Category category);

    @Mappings({
            @Mapping(target = "id", source = "productEntity.id"),
            @Mapping(target = "sellerId", source = "productEntity.sellerId"),
            @Mapping(target = "sellerName", source = "productEntity.sellerName"),
            @Mapping(target = "title", source = "productEntity.title"),
            @Mapping(target = "description", source = "productUpdateDTO.description"),
            @Mapping(target = "brand", source = "productUpdateDTO.brand"),
            @Mapping(target = "category", source = "category"),
            @Mapping(target = "pricePromotion", source = "productUpdateDTO.pricePromotion"),
            @Mapping(target = "price", source = "productUpdateDTO.price"),
            @Mapping(target = "stock", source = "productUpdateDTO.stock"),
            @Mapping(target = "imageUrl", source = "productUpdateDTO.imageUrl"),
            @Mapping(target = "additionalInfo", source = "productUpdateDTO.additionalInfo"),
            @Mapping(target = "defaultDateTime", source = "productEntity.defaultDateTime")
    })
    ProductEntity toEntity(ProductUpdateDTO productUpdateDTO, ProductEntity productEntity, Category category);

    @Mappings({
            @Mapping(target = "id", source = "productEntity.id"),
            @Mapping(target = "sellerId", source = "productEntity.sellerId"),
            @Mapping(target = "sellerName", source = "productEntity.sellerName"),
            @Mapping(target = "title", source = "productEntity.title"),
            @Mapping(target = "description", source = "productEntity.description"),
            @Mapping(target = "brand", source = "productEntity.brand"),
            @Mapping(target = "category", source = "productEntity.category"),
            @Mapping(target = "pricePromotion", source = "productEntity.pricePromotion"),
            @Mapping(target = "price", source = "productEntity.price"),
            @Mapping(target = "stock", source = "stock"),
            @Mapping(target = "imageUrl", source = "productEntity.imageUrl"),
            @Mapping(target = "additionalInfo", source = "productEntity.additionalInfo"),
            @Mapping(target = "defaultDateTime", source = "productEntity.defaultDateTime")
    })
    ProductEntity toEntity(ProductEntity productEntity, Integer stock);

    @Mappings({
            @Mapping(target = "sellerId", source = "productEntity.sellerId", qualifiedByName = "getSellerId"),
            @Mapping(target = "sellerName", source = "productEntity.sellerName"),
            @Mapping(target = "title", source = "productEntity.title"),
            @Mapping(target = "description", source = "productEntity.description"),
            @Mapping(target = "brand", source = "productEntity.brand"),
            @Mapping(target = "category", source = "productEntity.category"),
            @Mapping(target = "pricePromotion", source = "productEntity.pricePromotion"),
            @Mapping(target = "price", source = "productEntity.price"),
            @Mapping(target = "stock", source = "productEntity.stock"),
            @Mapping(target = "imageUrl", source = "productEntity.imageUrl"),
            @Mapping(target = "additionalInfo", source = "productEntity.additionalInfo"),
            @Mapping(target = "defaultDateTime", source = "productEntity.defaultDateTime", qualifiedByName = "getDefaultDateTime")
    })
    ProductReturnDTO toReturnDTO(ProductEntity productEntity);

    @Mappings({
            @Mapping(target = "productId", source = "productEntity.id"),
            @Mapping(target = "title", source = "productEntity.title"),
            @Mapping(target = "pricePromotion", source = "productEntity.pricePromotion"),
            @Mapping(target = "price", source = "productEntity.price"),
            @Mapping(target = "quantity", source = "quantity")
    })
    ProductRequest toProductRequest(ProductEntity productEntity, Integer quantity);

    @Named("getSellerId")
    default String getSellerId(ObjectId sellerId) {
        return sellerId.toString();
    }

    @Named("getDefaultDateTime")
    default String getDefaultDateTime(DateTime defaultDateTime) {
        return defaultDateTime.toString();
    }
}
