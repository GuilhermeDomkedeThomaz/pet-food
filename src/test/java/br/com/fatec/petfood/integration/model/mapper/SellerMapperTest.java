package br.com.fatec.petfood.integration.model.mapper;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SellerMapperTest extends IntegrationTest {

    @Autowired
    private SellerMapper sellerMapper;

    @Test
    public void shouldMapperToEntity() {
        SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());

        SellerEntity sellerEntity = sellerMapper.toEntity(sellerDTO, passwordEncrypted, CityZone.EAST);

        Assertions.assertEquals(sellerDTO.getName(), sellerEntity.getName());
        Assertions.assertEquals(sellerDTO.getEmail(), sellerEntity.getEmail());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getDocument(), sellerEntity.getRegistrationInfos().getDocument());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getCellPhone(), sellerEntity.getRegistrationInfos().getCellPhone());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getAddress(), sellerEntity.getRegistrationInfos().getAddress());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getNumberAddress(), sellerEntity.getRegistrationInfos().getNumberAddress());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getCep(), sellerEntity.getRegistrationInfos().getCep());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getCity(), sellerEntity.getRegistrationInfos().getCity());
        Assertions.assertEquals(sellerDTO.getPassword(), new String(Base64.decodeBase64(sellerEntity.getPassword())));
        Assertions.assertEquals(CityZone.EAST, sellerEntity.getCityZone());
    }

    @Test
    public void shouldMapperToReturnDTO() {
        SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);

        SellerReturnDTO sellerReturnDTO = sellerMapper.toReturnDTO(sellerEntity);

        Assertions.assertEquals(sellerEntity.getName(), sellerReturnDTO.getName());
        Assertions.assertEquals(sellerEntity.getEmail(), sellerReturnDTO.getEmail());
        Assertions.assertEquals(sellerEntity.getRegistrationInfos().getDocument(), sellerReturnDTO.getRegistrationInfos().getDocument());
        Assertions.assertEquals(sellerEntity.getRegistrationInfos().getCellPhone(), sellerReturnDTO.getRegistrationInfos().getCellPhone());
        Assertions.assertEquals(sellerEntity.getRegistrationInfos().getAddress(), sellerReturnDTO.getRegistrationInfos().getAddress());
        Assertions.assertEquals(sellerEntity.getRegistrationInfos().getNumberAddress(), sellerReturnDTO.getRegistrationInfos().getNumberAddress());
        Assertions.assertEquals(sellerEntity.getRegistrationInfos().getCep(), sellerReturnDTO.getRegistrationInfos().getCep());
        Assertions.assertEquals(sellerEntity.getRegistrationInfos().getCity(), sellerReturnDTO.getRegistrationInfos().getCity());
        Assertions.assertEquals(sellerEntity.getCityZone(), sellerReturnDTO.getCityZone());
    }
}
