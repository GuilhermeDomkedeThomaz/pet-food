package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.impl.RequestValidationServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import br.com.fatec.petfood.utils.ValidateUtils;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

public class RequestValidationServiceTest extends UnitTest {

    @Mock
    private ValidateUtils validateUtils;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private RequestValidationServiceImpl requestValidationServiceImpl;

    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);
    private final RequestEntity requestEntity = EnhancedRandom.random(RequestEntity.class);
    private final ProductRequest productRequest = EnhancedRandom.random(ProductRequest.class);
    private final ProductRequestDTO firstProductRequestDTO = EnhancedRandom.random(ProductRequestDTO.class);
    private final ProductRequestDTO secondProductRequestDTO = EnhancedRandom.random(ProductRequestDTO.class);
    private final ProductRequestDTO invalidProductRequestDTO = EnhancedRandom.random(ProductRequestDTO.class, "quantity");
    private final List<ProductRequestDTO> productsRequestDTO = List.of(firstProductRequestDTO, secondProductRequestDTO);

    @Test
    public void shouldValidateShippingPriceRequestDTOWithSuccess() {
        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateShippingPrice(9.99));
    }

    @Test
    public void shouldValidateRequestDTOWithInvalidShippingPrice() {
        try {
            requestValidationServiceImpl.validateShippingPrice(null);
        } catch (Exception e) {
            Assertions.assertEquals("Valor de frete passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateShippingPrice(-9.99);
        } catch (Exception e) {
            Assertions.assertEquals("Valor de frete passado inválido(menor ou igual a zero).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerRequestDTOWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(sellerEntity));

        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateSellerRequestDTO(sellerEntity.getName()));
    }

    @Test
    public void shouldValidateSellerRequestDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            requestValidationServiceImpl.validateSellerRequestDTO(sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerRequestDTOWithNotExistsSeller() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(sellerRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

        try {
            requestValidationServiceImpl.validateSellerRequestDTO(sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lojista não encontrado com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserRequestDTOWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(userEntity));

        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateUserRequestDTO(userEntity.getName()));
    }

    @Test
    public void shouldValidateUserRequestDTOWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            requestValidationServiceImpl.validateUserRequestDTO(userEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Nome do usuário passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserRequestDTOWithNotExistsUser() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

        try {
            requestValidationServiceImpl.validateUserRequestDTO(userEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Usuário não encontrado com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithSuccess() {
        productEntity.setStock(10);
        productsRequestDTO.forEach(productRequest -> productRequest.setQuantity(5));

        Mockito.when(productRepository.findByTitleAndSellerName(Mockito.anyString(), eq(sellerEntity.getName()))).thenReturn(Optional.of(productEntity));
        Mockito.when(productMapper.toProductRequest(productEntity, 5)).thenReturn(productRequest);

        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateProductsRequestDTO(productsRequestDTO, sellerEntity.getName()));
    }

    @Test
    public void shouldValidateProductsRequestDTOWithInvalidProducts() {
        try {
            requestValidationServiceImpl.validateProductsRequestDTO(null, sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(new ArrayList<>(), sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithNullQuantity() {
        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(invalidProductRequestDTO), sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + invalidProductRequestDTO.getTitle() + "} passado com estoque inválido(vazio ou nulo).] ", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithInvalidQuantity() {
        firstProductRequestDTO.setQuantity(0);

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(firstProductRequestDTO), sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + firstProductRequestDTO.getTitle() + "} passado com estoque inválido(menor ou igual a 0).] ", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithInvalidExistsStock() {
        productEntity.setStock(5);
        firstProductRequestDTO.setQuantity(10);

        Mockito.when(productRepository.findByTitleAndSellerName(Mockito.anyString(), eq(sellerEntity.getName()))).thenReturn(Optional.of(productEntity));

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(firstProductRequestDTO), sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + firstProductRequestDTO.getTitle() + "} não tem estoque necessário. Estoque solicitado: {" + firstProductRequestDTO.getQuantity() +
                    "}, estoque atual: {" + productEntity.getStock() + "}] ", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOAndNotExistsProduct() {
        firstProductRequestDTO.setQuantity(10);
        Mockito.when(productRepository.findByTitleAndSellerName(Mockito.anyString(), eq(sellerEntity.getName()))).thenReturn(Optional.empty());

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(firstProductRequestDTO), sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + firstProductRequestDTO.getTitle() + "} não encontrado para o lojista passado.] ", e.getMessage());
        }
    }

    @Test
    public void shouldValidateRequestEntityTotalValueWithSuccess() {
        requestEntity.setTotalValue(99.99);
        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateRequestEntityTotalValue(requestEntity));
    }

    @Test
    public void shouldValidateInvalidRequestEntityTotalValue() {
        requestEntity.setTotalValue(null);

        try {
            requestValidationServiceImpl.validateRequestEntityTotalValue(requestEntity);
        } catch (Exception e) {
            Assertions.assertEquals("Erro no mapeamento para criação do pedido: Valor total não mapeado.",
                    e.getMessage());
        }

        requestEntity.setTotalValue(-99.99);

        try {
            requestValidationServiceImpl.validateRequestEntityTotalValue(requestEntity);
        } catch (Exception e) {
            Assertions.assertEquals("Erro no mapeamento para criação do pedido: Valor total inválido(menor ou igual a zero).",
                    e.getMessage());
        }
    }

    @Test
    public void shouldValidateFindRequestsWithSuccess() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.TRUE);

        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateFindRequestById(requestEntity.getId().toString()));
        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateFindRequestBySeller(sellerEntity.getName()));
        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateFindRequestByUser(userEntity.getName()));
    }

    @Test
    public void shouldValidateFindRequestsWithInvalidParams() {
        Mockito.when(validateUtils.isNotNullAndNotEmpty(Mockito.anyString())).thenReturn(Boolean.FALSE);

        try {
            requestValidationServiceImpl.validateFindRequestById("");
        } catch (Exception e) {
            Assertions.assertEquals("Id do pedido passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateFindRequestBySeller("");
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateFindRequestByUser("");
        } catch (Exception e) {
            Assertions.assertEquals("Nome do usuário passado inválido(vazio ou nulo).", e.getMessage());
        }
    }
}
