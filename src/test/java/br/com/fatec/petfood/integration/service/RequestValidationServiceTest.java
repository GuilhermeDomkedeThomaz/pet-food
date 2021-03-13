package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.impl.RequestValidationServiceImpl;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class RequestValidationServiceTest extends IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RequestValidationServiceImpl requestValidationServiceImpl;

    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);
    private final RequestEntity requestEntity = EnhancedRandom.random(RequestEntity.class);

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
        sellerRepository.save(sellerEntity);

        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateSellerRequestDTO(sellerEntity.getName()));
    }

    @Test
    public void shouldValidateSellerRequestDTOWithInvalidParams() {
        try {
            requestValidationServiceImpl.validateSellerRequestDTO(null);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateSellerRequestDTOWithNotExistsSeller() {
        try {
            requestValidationServiceImpl.validateSellerRequestDTO(sellerEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Lojista não encontrado com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserRequestDTOWithSuccess() {
        userRepository.save(userEntity);

        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateUserRequestDTO(userEntity.getName()));
    }

    @Test
    public void shouldValidateUserRequestDTOWithInvalidParams() {
        try {
            requestValidationServiceImpl.validateUserRequestDTO(null);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do usuário passado inválido(vazio ou nulo).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateUserRequestDTOWithNotExistsUser() {
        try {
            requestValidationServiceImpl.validateUserRequestDTO(userEntity.getName());
        } catch (Exception e) {
            Assertions.assertEquals("Usuário não encontrado com o nome passado.", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithSuccess() {
        productEntity.setStock(10);
        productRepository.save(productEntity);
        final ProductRequestDTO productRequestDTO = new ProductRequestDTO(productEntity.getTitle(), 5);

        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateProductsRequestDTO(List.of(productRequestDTO), productEntity.getSellerName()));
    }

    @Test
    public void shouldValidateProductsRequestDTOWithInvalidProducts() {
        try {
            requestValidationServiceImpl.validateProductsRequestDTO(null, productEntity.getSellerName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(new ArrayList<>(), productEntity.getSellerName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithNullQuantity() {
        final ProductRequestDTO productRequestDTO = new ProductRequestDTO(productEntity.getTitle(), null);

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(productRequestDTO), productEntity.getSellerName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + productRequestDTO.getTitle() + "} passado com estoque inválido(vazio ou nulo).] ", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithInvalidQuantity() {
        final ProductRequestDTO productRequestDTO = new ProductRequestDTO(productEntity.getTitle(), -1);

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(productRequestDTO), productEntity.getSellerName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + productRequestDTO.getTitle() + "} passado com estoque inválido(menor ou igual a 0).] ", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOWithInvalidExistsStock() {
        productEntity.setStock(5);
        productRepository.save(productEntity);
        final ProductRequestDTO productRequestDTO = new ProductRequestDTO(productEntity.getTitle(), 10);

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(productRequestDTO), productEntity.getSellerName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + productRequestDTO.getTitle() + "} não tem estoque necessário. Estoque solicitado: {" + productRequestDTO.getQuantity() + "}, estoque atual: {"
                    + productEntity.getStock() + "}] ", e.getMessage());
        }
    }

    @Test
    public void shouldValidateProductsRequestDTOAndNotExistsProduct() {
        final ProductRequestDTO productRequestDTO = new ProductRequestDTO(productEntity.getTitle(), 10);

        try {
            requestValidationServiceImpl.validateProductsRequestDTO(List.of(productRequestDTO), productEntity.getSellerName());
        } catch (Exception e) {
            Assertions.assertEquals("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                    + productRequestDTO.getTitle() + "} não encontrado para o lojista passado.] ", e.getMessage());
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
        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateFindRequestById(requestEntity.getId().toString()));
        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateFindRequestBySeller(sellerEntity.getName()));
        Assertions.assertDoesNotThrow(() -> requestValidationServiceImpl.validateFindRequestByUser(userEntity.getName()));
    }

    @Test
    public void shouldValidateFindRequestsWithInvalidParams() {
        try {
            requestValidationServiceImpl.validateFindRequestById(null);
        } catch (Exception e) {
            Assertions.assertEquals("Id do pedido passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateFindRequestById("");
        } catch (Exception e) {
            Assertions.assertEquals("Id do pedido passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateFindRequestBySeller(null);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateFindRequestBySeller("");
        } catch (Exception e) {
            Assertions.assertEquals("Nome do lojista passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateFindRequestByUser(null);
        } catch (Exception e) {
            Assertions.assertEquals("Nome do usuário passado inválido(vazio ou nulo).", e.getMessage());
        }

        try {
            requestValidationServiceImpl.validateFindRequestByUser("");
        } catch (Exception e) {
            Assertions.assertEquals("Nome do usuário passado inválido(vazio ou nulo).", e.getMessage());
        }
    }
}
