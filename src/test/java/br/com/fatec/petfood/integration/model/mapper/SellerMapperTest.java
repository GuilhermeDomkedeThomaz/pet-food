package br.com.fatec.petfood.integration.model.mapper;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class SellerMapperTest extends IntegrationTest {

    @Autowired
    private SellerMapper sellerMapper;

    @Test
    public void shouldMapperToEntity() {
        SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
        sellerDTO.setWeekInitialTimeOperation("08:00");
        sellerDTO.setWeekFinalTimeOperation("18:00");
        sellerDTO.setWeekendInitialTimeOperation("10:00");
        sellerDTO.setWeekendFinalTimeOperation("16:00");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());
        List<Category> categories = Arrays.asList(Category.FOOD, Category.OTHERS);

        SellerEntity sellerEntity = sellerMapper.toEntity(sellerDTO, passwordEncrypted, CityZone.EAST, categories);

        Assertions.assertEquals(sellerDTO.getName(), sellerEntity.getName());
        Assertions.assertEquals(sellerDTO.getEmail(), sellerEntity.getEmail());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getDocument(), sellerEntity.getRegistrationInfos().getDocument());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getCellPhone(), sellerEntity.getRegistrationInfos().getCellPhone());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getAddress(), sellerEntity.getRegistrationInfos().getAddress());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getNumberAddress(), sellerEntity.getRegistrationInfos().getNumberAddress());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getCep(), sellerEntity.getRegistrationInfos().getCep());
        Assertions.assertEquals(sellerDTO.getRegistrationInfos().getCity(), sellerEntity.getRegistrationInfos().getCity());
        Assertions.assertEquals(sellerDTO.getPassword(), new String(Base64.decodeBase64(sellerEntity.getPassword())));
        Assertions.assertEquals(sellerDTO.getImageUrl(), sellerEntity.getImageUrl());
        Assertions.assertEquals(LocalTime.parse(sellerDTO.getWeekInitialTimeOperation()), sellerEntity.getWeekInitialTimeOperation());
        Assertions.assertEquals(LocalTime.parse(sellerDTO.getWeekendFinalTimeOperation()), sellerEntity.getWeekendFinalTimeOperation());
        Assertions.assertEquals(LocalTime.parse(sellerDTO.getWeekendInitialTimeOperation()), sellerEntity.getWeekendInitialTimeOperation());
        Assertions.assertEquals(LocalTime.parse(sellerDTO.getWeekendFinalTimeOperation()), sellerEntity.getWeekendFinalTimeOperation());
        Assertions.assertEquals(CityZone.EAST, sellerEntity.getCityZone());
        Assertions.assertEquals(categories, sellerEntity.getCategories());
    }

    @Test
    public void shouldMapperToEntityUpdate() {
        SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
        SellerUpdateDTO sellerUpdateDTO = EnhancedRandom.random(SellerUpdateDTO.class);
        sellerUpdateDTO.setWeekInitialTimeOperation("08:00");
        sellerUpdateDTO.setWeekFinalTimeOperation("18:00");
        sellerUpdateDTO.setWeekendInitialTimeOperation("10:00");
        sellerUpdateDTO.setWeekendFinalTimeOperation("16:00");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerUpdateDTO.getPassword().getBytes());
        List<Category> categories = Arrays.asList(Category.FOOD, Category.OTHERS);
        RegistrationInfos registrationInfos = new RegistrationInfos(
                sellerEntity.getRegistrationInfos().getDocument(),
                sellerUpdateDTO.getCellPhone(),
                sellerUpdateDTO.getAddress(),
                sellerUpdateDTO.getNumberAddress(),
                sellerUpdateDTO.getCep(),
                sellerUpdateDTO.getCity(),
                sellerUpdateDTO.getUf()
        );

        SellerEntity sellerEntityUpdate = sellerMapper.toEntity(sellerEntity, sellerUpdateDTO, registrationInfos,
                passwordEncrypted, CityZone.EAST, categories);

        Assertions.assertEquals(sellerEntity.getId(), sellerEntityUpdate.getId());
        Assertions.assertEquals(sellerEntity.getName(), sellerEntityUpdate.getName());
        Assertions.assertEquals(sellerUpdateDTO.getEmail(), sellerEntityUpdate.getEmail());
        Assertions.assertEquals(sellerEntity.getRegistrationInfos().getDocument(),
                sellerEntityUpdate.getRegistrationInfos().getDocument());
        Assertions.assertEquals(sellerUpdateDTO.getCellPhone(), sellerEntityUpdate.getRegistrationInfos().getCellPhone());
        Assertions.assertEquals(sellerUpdateDTO.getAddress(), sellerEntityUpdate.getRegistrationInfos().getAddress());
        Assertions.assertEquals(sellerUpdateDTO.getNumberAddress(), sellerEntityUpdate.getRegistrationInfos().getNumberAddress());
        Assertions.assertEquals(sellerUpdateDTO.getCep(), sellerEntityUpdate.getRegistrationInfos().getCep());
        Assertions.assertEquals(sellerUpdateDTO.getCity(), sellerEntityUpdate.getRegistrationInfos().getCity());
        Assertions.assertEquals(sellerUpdateDTO.getPassword(), new String(Base64.decodeBase64(sellerEntityUpdate.getPassword())));
        Assertions.assertEquals(sellerUpdateDTO.getImageUrl(), sellerEntityUpdate.getImageUrl());
        Assertions.assertEquals(LocalTime.parse(sellerUpdateDTO.getWeekInitialTimeOperation()), sellerEntityUpdate.getWeekInitialTimeOperation());
        Assertions.assertEquals(LocalTime.parse(sellerUpdateDTO.getWeekendFinalTimeOperation()), sellerEntityUpdate.getWeekendFinalTimeOperation());
        Assertions.assertEquals(LocalTime.parse(sellerUpdateDTO.getWeekendInitialTimeOperation()), sellerEntityUpdate.getWeekendInitialTimeOperation());
        Assertions.assertEquals(LocalTime.parse(sellerUpdateDTO.getWeekendFinalTimeOperation()), sellerEntityUpdate.getWeekendFinalTimeOperation());
        Assertions.assertEquals(CityZone.EAST, sellerEntityUpdate.getCityZone());
        Assertions.assertEquals(categories, sellerEntityUpdate.getCategories());
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
        Assertions.assertEquals(sellerEntity.getImageUrl(), sellerReturnDTO.getImageUrl());
        Assertions.assertEquals(DateTimeFormatter.ISO_LOCAL_TIME.format(sellerEntity.getWeekInitialTimeOperation()),
                sellerReturnDTO.getWeekInitialTimeOperation());
        Assertions.assertEquals(DateTimeFormatter.ISO_LOCAL_TIME.format(sellerEntity.getWeekFinalTimeOperation()),
                sellerReturnDTO.getWeekFinalTimeOperation());
        Assertions.assertEquals(DateTimeFormatter.ISO_LOCAL_TIME.format(sellerEntity.getWeekendInitialTimeOperation()),
                sellerReturnDTO.getWeekendInitialTimeOperation());
        Assertions.assertEquals(DateTimeFormatter.ISO_LOCAL_TIME.format(sellerEntity.getWeekendFinalTimeOperation()),
                sellerReturnDTO.getWeekendFinalTimeOperation());
        Assertions.assertEquals(sellerEntity.getCategories(), sellerReturnDTO.getCategories());
    }
}
