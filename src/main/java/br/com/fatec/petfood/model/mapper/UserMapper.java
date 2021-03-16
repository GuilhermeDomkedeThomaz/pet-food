package br.com.fatec.petfood.model.mapper;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserReturnDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "name", source = "userDTO.name"),
            @Mapping(target = "email", source = "userDTO.email"),
            @Mapping(target = "password", source = "passwordEncrypted"),
            @Mapping(target = "registrationInfos", source = "userDTO.registrationInfos"),
            @Mapping(target = "birthdayDate", source = "userDTO.birthdayDate"),
            @Mapping(target = "cityZone", source = "cityZone"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    UserEntity toEntity(UserDTO userDTO, byte[] passwordEncrypted, CityZone cityZone);

    @Mappings({
            @Mapping(target = "id", source = "userEntity.id"),
            @Mapping(target = "name", source = "userEntity.name"),
            @Mapping(target = "email", source = "userUpdateDTO.email"),
            @Mapping(target = "password", source = "passwordEncrypted"),
            @Mapping(target = "registrationInfos", source = "registrationInfos"),
            @Mapping(target = "birthdayDate", source = "userUpdateDTO.birthdayDate"),
            @Mapping(target = "cityZone", source = "cityZone"),
            @Mapping(target = "defaultDateTime", source = "userEntity.defaultDateTime")
    })
    UserEntity toEntity(UserEntity userEntity, UserUpdateDTO userUpdateDTO, RegistrationInfos registrationInfos,
                        byte[] passwordEncrypted, CityZone cityZone);

    @Mappings({
            @Mapping(target = "name", source = "user.name"),
            @Mapping(target = "email", source = "user.email"),
            @Mapping(target = "registrationInfos", source = "user.registrationInfos"),
            @Mapping(target = "birthdayDate", source = "user.birthdayDate"),
            @Mapping(target = "cityZone", source = "user.cityZone"),
            @Mapping(target = "defaultDateTime", source = "user.defaultDateTime", qualifiedByName = "getDefaultDateTime")
    })
    UserReturnDTO toReturnDTO(UserEntity user);

    @Named("getDefaultDateTime")
    default String getDefaultDateTime(DateTime defaultDateTime) {
        return defaultDateTime.toString();
    }
}
