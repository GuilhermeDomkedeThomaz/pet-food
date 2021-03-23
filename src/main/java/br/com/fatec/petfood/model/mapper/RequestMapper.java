package br.com.fatec.petfood.model.mapper;

import br.com.fatec.petfood.model.dto.RequestReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.model.generic.ProductRequest;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    @Mappings({
            @Mapping(target = "sellerId", source = "sellerId"),
            @Mapping(target = "sellerName", source = "sellerName"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "userName", source = "userName"),
            @Mapping(target = "products", source = "productRequests"),
            @Mapping(target = "totalPricePromotion", source = "productRequests", qualifiedByName = "getTotalPricePromotion"),
            @Mapping(target = "totalPrice", source = "productRequests", qualifiedByName = "getTotalPrice"),
            @Mapping(target = "totalQuantity", source = "productRequests", qualifiedByName = "getTotalQuantity"),
            @Mapping(target = "shippingPrice", source = "shippingPrice"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    RequestEntity toEntity(ObjectId sellerId, String sellerName, ObjectId userId, String userName,
                           List<ProductRequest> productRequests, Double shippingPrice, Status status);

    @Mappings({
            @Mapping(target = "id", source = "requestEntity.id"),
            @Mapping(target = "sellerId", source = "requestEntity.sellerId"),
            @Mapping(target = "sellerName", source = "requestEntity.sellerName"),
            @Mapping(target = "userId", source = "requestEntity.userId"),
            @Mapping(target = "userName", source = "requestEntity.userName"),
            @Mapping(target = "products", source = "productRequests"),
            @Mapping(target = "totalPricePromotion", source = "productRequests", qualifiedByName = "getTotalPricePromotion"),
            @Mapping(target = "totalPrice", source = "productRequests", qualifiedByName = "getTotalPrice"),
            @Mapping(target = "totalQuantity", source = "productRequests", qualifiedByName = "getTotalQuantity"),
            @Mapping(target = "shippingPrice", source = "shippingPrice"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "rate", source = "requestEntity.rate"),
            @Mapping(target = "defaultDateTime", source = "requestEntity.defaultDateTime"),
            @Mapping(target = "lastUpdateDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    RequestEntity toEntity(RequestEntity requestEntity, List<ProductRequest> productRequests,
                           Double shippingPrice, Status status);

    @Mappings({
            @Mapping(target = "id", source = "requestEntity.id"),
            @Mapping(target = "sellerId", source = "requestEntity.sellerId"),
            @Mapping(target = "sellerName", source = "requestEntity.sellerName"),
            @Mapping(target = "userId", source = "requestEntity.userId"),
            @Mapping(target = "userName", source = "requestEntity.userName"),
            @Mapping(target = "products", source = "requestEntity.products"),
            @Mapping(target = "totalPricePromotion", source = "requestEntity.totalPricePromotion"),
            @Mapping(target = "totalPrice", source = "requestEntity.totalPrice"),
            @Mapping(target = "totalQuantity", source = "requestEntity.totalQuantity"),
            @Mapping(target = "shippingPrice", source = "requestEntity.shippingPrice"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "rate", source = "requestEntity.rate"),
            @Mapping(target = "defaultDateTime", source = "requestEntity.defaultDateTime"),
            @Mapping(target = "lastUpdateDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    RequestEntity toEntity(RequestEntity requestEntity, Status status);

    @Mappings({
            @Mapping(target = "id", source = "requestEntity.id"),
            @Mapping(target = "sellerId", source = "requestEntity.sellerId"),
            @Mapping(target = "sellerName", source = "requestEntity.sellerName"),
            @Mapping(target = "userId", source = "requestEntity.userId"),
            @Mapping(target = "userName", source = "requestEntity.userName"),
            @Mapping(target = "products", source = "requestEntity.products"),
            @Mapping(target = "totalPricePromotion", source = "requestEntity.totalPricePromotion"),
            @Mapping(target = "totalPrice", source = "requestEntity.totalPrice"),
            @Mapping(target = "totalQuantity", source = "requestEntity.totalQuantity"),
            @Mapping(target = "shippingPrice", source = "requestEntity.shippingPrice"),
            @Mapping(target = "status", source = "requestEntity.status"),
            @Mapping(target = "rate", source = "rate"),
            @Mapping(target = "defaultDateTime", source = "requestEntity.defaultDateTime"),
            @Mapping(target = "lastUpdateDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    RequestEntity toEntity(RequestEntity requestEntity, Integer rate);

    @Mappings({
            @Mapping(target = "id", source = "requestEntity.id", qualifiedByName = "getId"),
            @Mapping(target = "sellerName", source = "requestEntity.sellerName"),
            @Mapping(target = "userName", source = "requestEntity.userName"),
            @Mapping(target = "products", source = "requestEntity.products"),
            @Mapping(target = "totalPricePromotion", source = "requestEntity.totalPricePromotion"),
            @Mapping(target = "totalPrice", source = "requestEntity.totalPrice"),
            @Mapping(target = "totalQuantity", source = "requestEntity.totalQuantity"),
            @Mapping(target = "shippingPrice", source = "requestEntity.shippingPrice"),
            @Mapping(target = "totalValue", source = "requestEntity.totalValue"),
            @Mapping(target = "status", source = "requestEntity.status"),
            @Mapping(target = "defaultDateTime", source = "requestEntity.defaultDateTime", qualifiedByName = "getDefaultDateTime"),
            @Mapping(target = "lastUpdateDateTime", source = "requestEntity.lastUpdateDateTime", qualifiedByName = "getLastUpdateDateTime")
    })
    RequestReturnDTO toReturnDTO(RequestEntity requestEntity);

    @AfterMapping
    default void setTotalValue(@MappingTarget RequestEntity requestEntity) {
        requestEntity.setTotalValue(requestEntity.getTotalPrice() + requestEntity.getShippingPrice());
    }

    @Named("getTotalPricePromotion")
    default Double getTotalPricePromotion(List<ProductRequest> productRequests) {
        double totalPricePromotion = 0.0;

        for (ProductRequest product : productRequests) {
            totalPricePromotion = totalPricePromotion + (product.getPricePromotion() * product.getQuantity());
        }

        return totalPricePromotion;
    }

    @Named("getTotalPrice")
    default Double getTotalPrice(List<ProductRequest> productRequests) {
        double totalPrice = 0.0;

        for (ProductRequest product : productRequests) {
            totalPrice = totalPrice + (product.getPrice() * product.getQuantity());
        }

        return totalPrice;
    }

    @Named("getTotalQuantity")
    default Integer getTotalQuantity(List<ProductRequest> productRequests) {
        int totalQuantity = 0;

        for (ProductRequest product : productRequests) {
            totalQuantity = totalQuantity + product.getQuantity();
        }

        return totalQuantity;
    }

    @Named("getId")
    default String getId(ObjectId id) {
        return id.toString();
    }

    @Named("getDefaultDateTime")
    default String getDefaultDateTime(DateTime defaultDateTime) {
        return defaultDateTime.toString();
    }

    @Named("getLastUpdateDateTime")
    default String getLastUpdateDateTime(DateTime lastUpdateDateTime) {
        if (!Objects.isNull(lastUpdateDateTime)) {
            return lastUpdateDateTime.toString();
        } else
            return "";
    }
}
