package br.com.fatec.petfood.model.mapper;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "name", source = "userDTO.name"),
            @Mapping(target = "email", source = "userDTO.email"),
            @Mapping(target = "password", source = "passwordEncrypted"),
            @Mapping(target = "registrationInfos", source = "userDTO.registrationInfos"),
            @Mapping(target = "birthdayDate", source = "userDTO.birthdayDate"),
            @Mapping(target = "cityZone", source = "cityZone"),
            @Mapping(target = "pets", source = "pets"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    UserEntity toEntity(UserDTO userDTO, byte[] passwordEncrypted, Pets pets, CityZone cityZone);

    @Mappings({
            @Mapping(target = "name", source = "user.name"),
            @Mapping(target = "email", source = "user.email"),
            @Mapping(target = "registrationInfos", source = "user.registrationInfos"),
            @Mapping(target = "birthdayDate", source = "user.birthdayDate"),
            @Mapping(target = "cityZone", source = "user.cityZone"),
            @Mapping(target = "pets", source = "user.pets"),
            @Mapping(target = "defaultDateTime", source = "user.defaultDateTime", qualifiedByName = "getDefaultDateTime")
    })
    UserReturnDTO toReturnDTO(UserEntity user);

    @Named("getDefaultDateTime")
    default String getDefaultDateTime(DateTime defaultDateTime) {
        return defaultDateTime.toString();
    }
}
