package br.com.fatec.petfood.integration.model.mapper;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserReturnDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.mapper.UserMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMapperTest extends IntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void shouldMapperToEntity() {
        UserDTO userDTO = EnhancedRandom.random(UserDTO.class);
        byte[] passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());

        UserEntity userEntity = userMapper.toEntity(userDTO, passwordEncrypted, CityZone.EAST);

        Assertions.assertEquals(userDTO.getName(), userEntity.getName());
        Assertions.assertEquals(userDTO.getEmail(), userEntity.getEmail());
        Assertions.assertEquals(userDTO.getRegistrationInfos().getDocument(), userEntity.getRegistrationInfos().getDocument());
        Assertions.assertEquals(userDTO.getRegistrationInfos().getCellPhone(), userEntity.getRegistrationInfos().getCellPhone());
        Assertions.assertEquals(userDTO.getBirthdayDate(), userEntity.getBirthdayDate());
        Assertions.assertEquals(userDTO.getRegistrationInfos().getAddress(), userEntity.getRegistrationInfos().getAddress());
        Assertions.assertEquals(userDTO.getRegistrationInfos().getNumberAddress(), userEntity.getRegistrationInfos().getNumberAddress());
        Assertions.assertEquals(userDTO.getRegistrationInfos().getCep(), userEntity.getRegistrationInfos().getCep());
        Assertions.assertEquals(userDTO.getRegistrationInfos().getCity(), userEntity.getRegistrationInfos().getCity());
        Assertions.assertEquals(userDTO.getPassword(), new String(Base64.decodeBase64(userEntity.getPassword())));
        Assertions.assertEquals(CityZone.EAST, userEntity.getCityZone());
    }

    @Test
    public void shouldMapperToEntityUpdate() {
        UserUpdateDTO userUpdateDTO = EnhancedRandom.random(UserUpdateDTO.class);
        UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
        byte[] passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());

        UserEntity userEntityUpdate = userMapper.toEntity(userEntity, userUpdateDTO, passwordEncrypted, CityZone.EAST);

        Assertions.assertEquals(userEntity.getName(), userEntityUpdate.getName());
        Assertions.assertEquals(userUpdateDTO.getEmail(), userEntityUpdate.getEmail());
        Assertions.assertEquals(userUpdateDTO.getRegistrationInfos().getDocument(), userEntityUpdate.getRegistrationInfos().getDocument());
        Assertions.assertEquals(userUpdateDTO.getRegistrationInfos().getCellPhone(), userEntityUpdate.getRegistrationInfos().getCellPhone());
        Assertions.assertEquals(userUpdateDTO.getBirthdayDate(), userEntityUpdate.getBirthdayDate());
        Assertions.assertEquals(userUpdateDTO.getRegistrationInfos().getAddress(), userEntityUpdate.getRegistrationInfos().getAddress());
        Assertions.assertEquals(userUpdateDTO.getRegistrationInfos().getNumberAddress(), userEntityUpdate.getRegistrationInfos().getNumberAddress());
        Assertions.assertEquals(userUpdateDTO.getRegistrationInfos().getCep(), userEntityUpdate.getRegistrationInfos().getCep());
        Assertions.assertEquals(userUpdateDTO.getRegistrationInfos().getCity(), userEntityUpdate.getRegistrationInfos().getCity());
        Assertions.assertEquals(userUpdateDTO.getPassword(), new String(Base64.decodeBase64(userEntityUpdate.getPassword())));
        Assertions.assertEquals(CityZone.EAST, userEntityUpdate.getCityZone());
    }

    @Test
    public void shouldMapperToReturnDTO() {
        UserEntity userEntity = EnhancedRandom.random(UserEntity.class);

        UserReturnDTO userReturnDTO = userMapper.toReturnDTO(userEntity);

        Assertions.assertEquals(userEntity.getName(), userReturnDTO.getName());
        Assertions.assertEquals(userEntity.getEmail(), userReturnDTO.getEmail());
        Assertions.assertEquals(userEntity.getRegistrationInfos().getDocument(), userReturnDTO.getRegistrationInfos().getDocument());
        Assertions.assertEquals(userEntity.getRegistrationInfos().getCellPhone(), userReturnDTO.getRegistrationInfos().getCellPhone());
        Assertions.assertEquals(userEntity.getBirthdayDate(), userReturnDTO.getBirthdayDate());
        Assertions.assertEquals(userEntity.getRegistrationInfos().getAddress(), userReturnDTO.getRegistrationInfos().getAddress());
        Assertions.assertEquals(userEntity.getRegistrationInfos().getNumberAddress(), userReturnDTO.getRegistrationInfos().getNumberAddress());
        Assertions.assertEquals(userEntity.getRegistrationInfos().getCep(), userReturnDTO.getRegistrationInfos().getCep());
        Assertions.assertEquals(userEntity.getRegistrationInfos().getCity(), userReturnDTO.getRegistrationInfos().getCity());
        Assertions.assertEquals(userEntity.getCityZone(), userReturnDTO.getCityZone());
    }
}
