package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import br.com.fatec.petfood.model.mapper.UserMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.impl.ValidationServiceImpl;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidationServiceTest extends IntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ValidationServiceImpl validationServiceImpl;

    private final UserDTO userDTO = EnhancedRandom.random(UserDTO.class);
    private final List<Category> emptyCategories = Collections.emptyList();
    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final ProductDTO productDTO = EnhancedRandom.random(ProductDTO.class);
    private final List<Category> categories = Arrays.asList(Category.FOOD, Category.OTHERS);
    private final UserDTO userDTOWithoutName = EnhancedRandom.random(UserDTO.class, "name");
    private final SellerDTO sellerDTOWithoutName = EnhancedRandom.random(SellerDTO.class, "name");
    private final ProductDTO productDTOWithoutName = EnhancedRandom.random(ProductDTO.class, "sellerName");

    @Test
    public void shouldValidateUserDTOWithSuccess() {
        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST));
    }

    @Test
    public void shouldValidateUserDTOWithInvalidParams() {
        try {
            validationServiceImpl.validateUserDTO(userDTOWithoutName, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Nome passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithExistsName() {
        userRepository.save(userMapper.toEntity(userDTO, Base64.encodeBase64(userDTO.getPassword().getBytes()), CityZone.EAST));

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Usuário já existe com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithExistsEmail() {
        userRepository.save(userMapper.toEntity(userDTO, Base64.encodeBase64(userDTO.getPassword().getBytes()), CityZone.EAST));
        userDTO.setName("Teste");

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Usuário já existe com o email passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithInvalidNumberAddress() {
        userDTO.getRegistrationInfos().setNumberAddress(0);

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Número do endereço passado inválido(igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithInvalidCityZone() {
        try {
            validationServiceImpl.validateUserDTO(userDTO, null);
        } catch (Exception e) {
            Assertions.assertEquals("Zona da cidade passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithSuccess() {
        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories));
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidParams() {
        try {
            validationServiceImpl.validateSellerDTO(sellerDTOWithoutName, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Nome passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithExistsName() {
        sellerRepository.save(sellerMapper.toEntity(sellerDTO, Base64.encodeBase64(sellerDTO.getPassword().getBytes()), CityZone.EAST, categories));

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista já existe com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithExistsEmail() {
        sellerRepository.save(sellerMapper.toEntity(sellerDTO, Base64.encodeBase64(sellerDTO.getPassword().getBytes()), CityZone.EAST, categories));
        sellerDTO.setName("Teste");

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista já existe com o email passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidNumberAddress() {
        sellerDTO.getRegistrationInfos().setNumberAddress(0);

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Número do endereço passado inválido(igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidCategories() {
        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, null);
        } catch (Exception e) {
            Assertions.assertEquals("Categoria passada inválida(vazia ou nula).", e.getMessage());
        }

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, emptyCategories);
        } catch (Exception e) {
            Assertions.assertEquals("Categoria passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidCityZone() {
        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, null, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Zona da cidade passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithSuccess() {
        sellerRepository.save(sellerMapper.toEntity(sellerDTO, Base64.encodeBase64(sellerDTO.getPassword().getBytes()), CityZone.EAST, categories));
        productDTO.setSellerName(sellerDTO.getName());

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateProductDTO(productDTO, Category.FOOD));
    }

    @Test
    public void shouldValidateProductDTOWithInvalidParams() {
        try {
            validationServiceImpl.validateProductDTO(productDTOWithoutName, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithNotExistsSeller() {
        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista não encontrado com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithAlreadyExistsProductTitle() {
        SellerEntity sellerEntity = sellerRepository
                .save(sellerMapper.toEntity(sellerDTO, Base64.encodeBase64(sellerDTO.getPassword().getBytes()), CityZone.EAST, categories));
        productRepository.save(productMapper.toEntity(productDTO, sellerEntity.getId(), sellerEntity.getName(), Category.FOOD));
        productDTO.setSellerName(sellerEntity.getName());

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Título passado já cadastrado para o lojista passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithInvalidCategory() {
        sellerRepository.save(sellerMapper.toEntity(sellerDTO, Base64.encodeBase64(sellerDTO.getPassword().getBytes()), CityZone.EAST, categories));
        productDTO.setSellerName(sellerDTO.getName());

        try {
            validationServiceImpl.validateProductDTO(productDTO, null);
        } catch (Exception e) {
            Assertions.assertEquals("Categoria passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithInvalidPricePromotion() {
        sellerRepository.save(sellerMapper.toEntity(sellerDTO, Base64.encodeBase64(sellerDTO.getPassword().getBytes()), CityZone.EAST, categories));
        productDTO.setSellerName(sellerDTO.getName());
        productDTO.setPricePromotion(0.0);

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Preço de promoção passado inválido(igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithInvalidPrice() {
        sellerRepository.save(sellerMapper.toEntity(sellerDTO, Base64.encodeBase64(sellerDTO.getPassword().getBytes()), CityZone.EAST, categories));
        productDTO.setSellerName(sellerDTO.getName());
        productDTO.setPrice(0.0);

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Preço passado inválido(igual a 0).", e.getMessage());
        }
    }
}
