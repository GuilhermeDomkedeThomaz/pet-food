package br.com.fatec.petfood.model.mapper;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {

    @Mappings({
            @Mapping(target = "name", source = "sellerDTO.name"),
            @Mapping(target = "email", source = "sellerDTO.email"),
            @Mapping(target = "password", source = "passwordEncrypted"),
            @Mapping(target = "registrationInfos", source = "sellerDTO.registrationInfos"),
            @Mapping(target = "imageUrl", source = "sellerDTO.imageUrl"),
            @Mapping(target = "weekInitialTimeOperation", source = "sellerDTO.weekInitialTimeOperation"),
            @Mapping(target = "weekFinalTimeOperation", source = "sellerDTO.weekFinalTimeOperation"),
            @Mapping(target = "weekendInitialTimeOperation", source = "sellerDTO.weekendInitialTimeOperation"),
            @Mapping(target = "weekendFinalTimeOperation", source = "sellerDTO.weekendFinalTimeOperation"),
            @Mapping(target = "cityZone", source = "cityZone"),
            @Mapping(target = "categories", source = "categories"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    SellerEntity toEntity(SellerDTO sellerDTO, byte[] passwordEncrypted, CityZone cityZone, List<Category> categories);

    @Mappings({
            @Mapping(target = "id", source = "sellerEntity.id"),
            @Mapping(target = "name", source = "sellerEntity.name"),
            @Mapping(target = "email", source = "sellerUpdateDTO.email"),
            @Mapping(target = "password", source = "passwordEncrypted"),
            @Mapping(target = "registrationInfos", source = "registrationInfos"),
            @Mapping(target = "imageUrl", source = "sellerUpdateDTO.imageUrl"),
            @Mapping(target = "weekInitialTimeOperation", source = "sellerUpdateDTO.weekInitialTimeOperation"),
            @Mapping(target = "weekFinalTimeOperation", source = "sellerUpdateDTO.weekFinalTimeOperation"),
            @Mapping(target = "weekendInitialTimeOperation", source = "sellerUpdateDTO.weekendInitialTimeOperation"),
            @Mapping(target = "weekendFinalTimeOperation", source = "sellerUpdateDTO.weekendFinalTimeOperation"),
            @Mapping(target = "cityZone", source = "cityZone"),
            @Mapping(target = "categories", source = "categories"),
            @Mapping(target = "defaultDateTime", source = "sellerEntity.defaultDateTime")
    })
    SellerEntity toEntity(SellerEntity sellerEntity, SellerUpdateDTO sellerUpdateDTO, RegistrationInfos registrationInfos,
                          byte[] passwordEncrypted, CityZone cityZone, List<Category> categories);

    @Mappings({
            @Mapping(target = "name", source = "seller.name"),
            @Mapping(target = "email", source = "seller.email"),
            @Mapping(target = "registrationInfos", source = "seller.registrationInfos"),
            @Mapping(target = "imageUrl", source = "seller.imageUrl"),
            @Mapping(target = "weekInitialTimeOperation", source = "seller.weekInitialTimeOperation"),
            @Mapping(target = "weekFinalTimeOperation", source = "seller.weekFinalTimeOperation"),
            @Mapping(target = "weekendInitialTimeOperation", source = "seller.weekendInitialTimeOperation"),
            @Mapping(target = "weekendFinalTimeOperation", source = "seller.weekendFinalTimeOperation"),
            @Mapping(target = "cityZone", source = "seller.cityZone"),
            @Mapping(target = "categories", source = "seller.categories"),
            @Mapping(target = "defaultDateTime", source = "seller.defaultDateTime", qualifiedByName = "getDefaultDateTime")
    })
    SellerReturnDTO toReturnDTO(SellerEntity seller);

    @Named("getDefaultDateTime")
    default String getDefaultDateTime(DateTime defaultDateTime) {
        return defaultDateTime.toString();
    }
}
