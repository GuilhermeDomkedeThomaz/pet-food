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
            @Mapping(target = "document", source = "userDTO.document"),
            @Mapping(target = "cellPhone", source = "userDTO.cellPhone"),
            @Mapping(target = "birthdayDate", source = "userDTO.birthdayDate"),
            @Mapping(target = "address", source = "userDTO.address"),
            @Mapping(target = "numberAddress", source = "userDTO.numberAddress"),
            @Mapping(target = "cep", source = "userDTO.cep"),
            @Mapping(target = "city", source = "userDTO.city"),
            @Mapping(target = "cityZone", source = "cityZone"),
            @Mapping(target = "pets", source = "pets"),
            @Mapping(target = "defaultDateTime", expression = "java(org.joda.time.DateTime.now())")
    })
    UserEntity toEntity(UserDTO userDTO, byte[] passwordEncrypted, Pets pets, CityZone cityZone);

    @Mappings({
            @Mapping(target = "name", source = "user.name"),
            @Mapping(target = "email", source = "user.email"),
            @Mapping(target = "document", source = "user.document"),
            @Mapping(target = "cellPhone", source = "user.cellPhone"),
            @Mapping(target = "birthdayDate", source = "user.birthdayDate"),
            @Mapping(target = "address", source = "user.address"),
            @Mapping(target = "numberAddress", source = "user.numberAddress"),
            @Mapping(target = "cep", source = "user.cep"),
            @Mapping(target = "city", source = "user.city"),
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
