package br.com.fatec.petfood.model.mapper;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {

    @Mappings({
            @Mapping(target = "name", source = "sellerDTO.name"),
            @Mapping(target = "email", source = "sellerDTO.email"),
            @Mapping(target = "password", source = "passwordEncrypted"),
            @Mapping(target = "registrationInfos", source = "sellerDTO.registrationInfos"),
            @Mapping(target = "cityZone", source = "cityZone"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    SellerEntity toEntity(SellerDTO sellerDTO, byte[] passwordEncrypted, CityZone cityZone);

    @Mappings({
            @Mapping(target = "name", source = "seller.name"),
            @Mapping(target = "email", source = "seller.email"),
            @Mapping(target = "registrationInfos", source = "seller.registrationInfos"),
            @Mapping(target = "cityZone", source = "seller.cityZone"),
            @Mapping(target = "defaultDateTime", source = "seller.defaultDateTime", qualifiedByName = "getDefaultDateTime")
    })
    SellerReturnDTO toReturnDTO(SellerEntity seller);

    @Named("getDefaultDateTime")
    default String getDefaultDateTime(DateTime defaultDateTime) {
        return defaultDateTime.toString();
    }
}
