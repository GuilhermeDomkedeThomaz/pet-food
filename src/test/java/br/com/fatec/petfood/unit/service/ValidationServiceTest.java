package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.impl.ValidationServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import br.com.fatec.petfood.utils.ValidateUtils;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

public class ValidationServiceTest extends UnitTest {

    @Mock
    private ValidateUtils validateUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ValidationServiceImpl validationServiceImpl;

    private final UserDTO userDTO = EnhancedRandom.random(UserDTO.class);
    private final List<Category> emptyCategories = Collections.emptyList();
    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final ProductDTO productDTO = EnhancedRandom.random(ProductDTO.class);
    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);
    private final List<Category> categories = Arrays.asList(Category.FOOD, Category.OTHERS);

    @BeforeEach
    public void setup() {
        sellerDTO.setWeekInitialTimeOperation("08:00");
        sellerDTO.setWeekFinalTimeOperation("18:00");
        sellerDTO.setWeekendInitialTimeOperation("10:00");
        sellerDTO.setWeekendFinalTimeOperation("16:00");
    }

    @Test
    public void shouldValidateUserDTOWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByDocument(eq(userDTO.getRegistrationInfos().getDocument())))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST));
    }

    @Test
    public void shouldValidateUserDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Nome passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithExistsName() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Usuário já existe com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithExistsEmail() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.of(userEntity));

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Usuário já existe com o email passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithExistsDocument() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByDocument(eq(userDTO.getRegistrationInfos().getDocument())))
                .thenReturn(Optional.of(userEntity));

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Usuário já existe com o CPF passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithInvalidNumberAddress() {
        userDTO.getRegistrationInfos().setNumberAddress(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateUserDTO(userDTO, CityZone.EAST);
        } catch (Exception e) {
            Assertions.assertEquals("Número do endereço passado inválido(igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithInvalidCityZone() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateUserDTO(userDTO, null);
        } catch (Exception e) {
            Assertions.assertEquals("Zona da cidade passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByDocument(eq(sellerDTO.getRegistrationInfos().getDocument())))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories));
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Nome passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithExistsName() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.of(sellerEntity));

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista já existe com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithExistsEmail() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.of(sellerEntity));

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista já existe com o email passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithExistsDocument() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByDocument(eq(sellerDTO.getRegistrationInfos().getDocument())))
                .thenReturn(Optional.of(sellerEntity));

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista já existe com o CNPJ passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidNumberAddress() {
        sellerDTO.getRegistrationInfos().setNumberAddress(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Número do endereço passado inválido(igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidCategories() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

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
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, null, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Zona da cidade passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidTimeOperation() {
        sellerDTO.setWeekInitialTimeOperation("AAAA");

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Horário inicial de funcionamento durante a semana passado inválido. Favor passar no seguinte formato: 'HH:MM'.", e.getMessage());
        }

        sellerDTO.setWeekInitialTimeOperation(null);

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO, CityZone.EAST, categories);
        } catch (Exception e) {
            Assertions.assertEquals("Horário inicial de funcionamento durante a semana passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithSuccess() {
        productDTO.setStock(5);
        productDTO.setPrice(9.99);
        productDTO.setPricePromotion(9.99);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(sellerEntity.getName())))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateProductDTO(productDTO, Category.FOOD));
    }

    @Test
    public void shouldValidateProductDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithNotExistsSeller() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista não encontrado com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithAlreadyExistsProductTitle() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(sellerEntity.getName())))
                .thenReturn(Optional.of(productEntity));

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Título passado já cadastrado para o lojista passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithInvalidCategory() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(sellerEntity.getName())))
                .thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateProductDTO(productDTO, null);
        } catch (Exception e) {
            Assertions.assertEquals("Categoria passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithInvalidPricePromotion() {
        productDTO.setPricePromotion(0.0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(sellerEntity.getName())))
                .thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Preço de promoção passado inválido(menor ou igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithInvalidPrice() {
        productDTO.setPrice(0.0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(sellerEntity.getName())))
                .thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateProductDTO(productDTO, Category.FOOD);
        } catch (Exception e) {
            Assertions.assertEquals("Preço passado inválido(menor ou igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductStockUpdateWithSuccess() {
        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateProductStockUpdate(10));
    }

    @Test
    public void shouldValidateProductStockUpdateWithInvalidStock() {
        try {
            validationServiceImpl.validateProductStockUpdate(null);
        } catch (Exception e) {
            Assertions.assertEquals("Estoque passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            validationServiceImpl.validateProductStockUpdate(-50);
        } catch (Exception e) {
            Assertions.assertEquals("Estoque passado inválido(menor ou igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateSearchSeller("Ração", CityZone.EAST, "18:00", 0, 100));
    }

    @Test
    public void shouldValidateSearchSellerWithInvalidLocalTime() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        try {
            validationServiceImpl.validateSearchSeller("Ração", CityZone.EAST, "AAAAA", 0, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerWithInvalidCityZone() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        try {
            validationServiceImpl.validateSearchSeller("Ração", null, "18:00", 0, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Zona da cidade passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerWithInvalidPageAndSize() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        try {
            validationServiceImpl.validateSearchSeller("Ração", CityZone.EAST, "18:00", null, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Página passada inválida(vazia ou nula).", e.getMessage());
        }

        try {
            validationServiceImpl.validateSearchSeller("Ração", CityZone.EAST, "18:00", 0, null);
        } catch (Exception e) {
            Assertions.assertEquals("Tamanho da página passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerProductsWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateSearchSellerProducts("Loja Teste", 0, 100));
    }

    @Test
    public void shouldValidateSearchSellerProductsWithInvalidSellerName() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            validationServiceImpl.validateSearchSellerProducts("", 0, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerProductsWithInvalidPageAndSize() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        try {
            validationServiceImpl.validateSearchSellerProducts("Loja Teste", null, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Página passada inválida(vazia ou nula).", e.getMessage());
        }

        try {
            validationServiceImpl.validateSearchSellerProducts("Loja Teste", 0, null);
        } catch (Exception e) {
            Assertions.assertEquals("Tamanho da página passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerByCategoryWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateSearchSellerByCategory(Category.FOOD, CityZone.EAST, "18:00", 0, 100));
    }

    @Test
    public void shouldValidateSearchSellerByCategoryWithInvalidLocalTime() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        try {
            validationServiceImpl.validateSearchSellerByCategory(Category.FOOD, CityZone.EAST, "AAAAA", 0, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerByCategoryWithInvalidCityZone() {
        try {
            validationServiceImpl.validateSearchSellerByCategory(Category.FOOD, null, "18:00", 0, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Zona da cidade passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSearchSellerByCategoryWithInvalidPageAndSize() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        try {
            validationServiceImpl.validateSearchSellerByCategory(Category.FOOD, CityZone.EAST, "18:00", null, 100);
        } catch (Exception e) {
            Assertions.assertEquals("Página passada inválida(vazia ou nula).", e.getMessage());
        }

        try {
            validationServiceImpl.validateSearchSellerByCategory(Category.FOOD, CityZone.EAST, "18:00", 0, null);
        } catch (Exception e) {
            Assertions.assertEquals("Tamanho da página passado inválido(vazio ou nulo).", e.getMessage());
        }
    }
}
