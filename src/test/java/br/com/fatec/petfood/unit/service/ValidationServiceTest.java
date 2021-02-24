package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.impl.ValidationServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import br.com.fatec.petfood.utils.ValidateUtils;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

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
    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final ProductDTO productDTO = EnhancedRandom.random(ProductDTO.class);
    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);

    @Test
    public void shouldValidateUserDTOWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateUserDTO(userDTO));
    }

    @Test
    public void shouldValidateUserDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            validationServiceImpl.validateUserDTO(userDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Nome passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithExistsName() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));

        try {
            validationServiceImpl.validateUserDTO(userDTO);
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
            validationServiceImpl.validateUserDTO(userDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Usuário já existe com o email passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserDTOWithInvalidNumberAddress() {
        userDTO.getRegistrationInfos().setNumberAddress(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateUserDTO(userDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Número do endereço passado inválido(igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateSellerDTO(sellerDTO));
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Nome passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithExistsName() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.of(sellerEntity));

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO);
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
            validationServiceImpl.validateSellerDTO(sellerDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Lojista já existe com o email passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerDTOWithInvalidNumberAddress() {
        sellerDTO.getRegistrationInfos().setNumberAddress(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateSellerDTO(sellerDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Número do endereço passado inválido(igual a 0).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(sellerEntity.getName())))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> validationServiceImpl.validateProductDTO(productDTO));
    }

    @Test
    public void shouldValidateProductDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            validationServiceImpl.validateProductDTO(productDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductDTOWithNotExistsSeller() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(eq(productDTO.getSellerName()))).thenReturn(Optional.empty());

        try {
            validationServiceImpl.validateProductDTO(productDTO);
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
            validationServiceImpl.validateProductDTO(productDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Título passado já cadastrado para o lojista passado.", e.getMessage());
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
            validationServiceImpl.validateProductDTO(productDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Preço de promoção passado inválido(igual a 0).", e.getMessage());
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
            validationServiceImpl.validateProductDTO(productDTO);
        } catch (Exception e) {
            Assertions.assertEquals("Preço passado inválido(igual a 0).", e.getMessage());
        }
    }
}
