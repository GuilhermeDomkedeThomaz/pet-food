package br.com.fatec.petfood.model.mapper;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.enums.Pets;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mappings({
            @Mapping(target = "sellerId", source = "sellerId"),
            @Mapping(target = "sellerName", source = "sellerName"),
            @Mapping(target = "title", source = "productDTO.title"),
            @Mapping(target = "description", source = "productDTO.description"),
            @Mapping(target = "brand", source = "productDTO.brand"),
            @Mapping(target = "category", source = "productDTO.category"),
            @Mapping(target = "pricePromotion", source = "productDTO.pricePromotion"),
            @Mapping(target = "price", source = "productDTO.price"),
            @Mapping(target = "stock", source = "productDTO.stock"),
            @Mapping(target = "imageUrl", source = "productDTO.imageUrl"),
            @Mapping(target = "pets", source = "pets"),
            @Mapping(target = "additionalInfo", source = "productDTO.additionalInfo"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    ProductEntity toEntity(ProductDTO productDTO, ObjectId sellerId, String sellerName, Pets pets);

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
            @Mapping(target = "pets", source = "productEntity.pets"),
            @Mapping(target = "additionalInfo", source = "productEntity.additionalInfo"),
            @Mapping(target = "defaultDateTime", source = "productEntity.defaultDateTime", qualifiedByName = "getDefaultDateTime")
    })
    ProductReturnDTO toReturnDTO(ProductEntity productEntity);

    @Named("getSellerId")
    default String getSellerId(ObjectId sellerId) {
        return sellerId.toString();
    }

    @Named("getDefaultDateTime")
    default String getDefaultDateTime(DateTime defaultDateTime) {
        return defaultDateTime.toString();
    }
}
