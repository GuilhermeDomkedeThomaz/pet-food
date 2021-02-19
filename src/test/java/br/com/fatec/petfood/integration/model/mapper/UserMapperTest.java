package br.com.fatec.petfood.integration.model.mapper;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
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

        UserEntity userEntity = userMapper.toEntity(userDTO, passwordEncrypted, Pets.CAT, CityZone.EAST);

        Assertions.assertEquals(userDTO.getName(), userEntity.getName());
        Assertions.assertEquals(userDTO.getDocument(), userEntity.getDocument());
        Assertions.assertEquals(userDTO.getCellPhone(), userEntity.getCellPhone());
        Assertions.assertEquals(userDTO.getBirthdayDate(), userEntity.getBirthdayDate());
        Assertions.assertEquals(userDTO.getAddress(), userEntity.getAddress());
        Assertions.assertEquals(userDTO.getNumberAddress(), userEntity.getNumberAddress());
        Assertions.assertEquals(userDTO.getCep(), userEntity.getCep());
        Assertions.assertEquals(userDTO.getCity(), userEntity.getCity());
        Assertions.assertEquals(userDTO.getPassword(), new String(Base64.decodeBase64(userEntity.getPassword())));
        Assertions.assertEquals(Pets.CAT, userEntity.getPets());
        Assertions.assertEquals(CityZone.EAST, userEntity.getCityZone());
    }

    @Test
    public void shouldMapperToReturnDTO() {
        UserEntity userEntity = EnhancedRandom.random(UserEntity.class);

        UserReturnDTO userReturnDTO = userMapper.toReturnDTO(userEntity);

        Assertions.assertEquals(userEntity.getName(), userReturnDTO.getName());
        Assertions.assertEquals(userEntity.getDocument(), userReturnDTO.getDocument());
        Assertions.assertEquals(userEntity.getCellPhone(), userReturnDTO.getCellPhone());
        Assertions.assertEquals(userEntity.getBirthdayDate(), userReturnDTO.getBirthdayDate());
        Assertions.assertEquals(userEntity.getAddress(), userReturnDTO.getAddress());
        Assertions.assertEquals(userEntity.getNumberAddress(), userReturnDTO.getNumberAddress());
        Assertions.assertEquals(userEntity.getCep(), userReturnDTO.getCep());
        Assertions.assertEquals(userEntity.getCity(), userReturnDTO.getCity());
        Assertions.assertEquals(userEntity.getPets(), userReturnDTO.getPets());
        Assertions.assertEquals(userEntity.getCityZone(), userReturnDTO.getCityZone());
    }
}
