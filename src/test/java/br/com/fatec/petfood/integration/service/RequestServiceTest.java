package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.RequestService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class RequestServiceTest extends IntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    private final RequestDTO requestDTO = EnhancedRandom.random(RequestDTO.class);
    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity firstProductEntity = EnhancedRandom.random(ProductEntity.class);
    private final ProductEntity secondProductEntity = EnhancedRandom.random(ProductEntity.class);

    @Test
    public void shouldCreateRequestWithSuccess() {
        userRepository.save(userEntity);
        sellerRepository.save(sellerEntity);
        firstProductEntity.setStock(5);
        firstProductEntity.setPrice(9.99);
        firstProductEntity.setPricePromotion(9.99);
        firstProductEntity.setSellerId(sellerEntity.getId());
        firstProductEntity.setSellerName(sellerEntity.getName());
        secondProductEntity.setStock(5);
        secondProductEntity.setPrice(9.99);
        secondProductEntity.setPricePromotion(9.99);
        secondProductEntity.setSellerId(sellerEntity.getId());
        secondProductEntity.setSellerName(sellerEntity.getName());
        productRepository.save(firstProductEntity);
        productRepository.save(secondProductEntity);
        final ProductRequestDTO firstProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), 1);
        final ProductRequestDTO secondProductRequestDTO = new ProductRequestDTO(secondProductEntity.getTitle(), 1);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Pedido registrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidSeller() {
        requestDTO.setSellerName(null);

        ResponseEntity<?> nullResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullResponse.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
        Assertions.assertEquals(nullResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        requestDTO.setSellerName(sellerEntity.getName());

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado com o nome passado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidUser() {
        sellerRepository.save(sellerEntity);
        requestDTO.setSellerName(sellerEntity.getName());

        requestDTO.setUserName(null);

        ResponseEntity<?> nullResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullResponse.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");
        Assertions.assertEquals(nullResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        requestDTO.setUserName(userEntity.getName());

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado com o nome passado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidProducts() {
        userRepository.save(userEntity);
        sellerRepository.save(sellerEntity);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(null);

        ResponseEntity<?> nullResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).");
        Assertions.assertEquals(nullResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        requestDTO.setProducts(new ArrayList<>());

        ResponseEntity<?> emptyResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(emptyResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(emptyResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).");
        Assertions.assertEquals(emptyResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        final ProductRequestDTO notExistsProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), 1);
        requestDTO.setProducts(List.of(notExistsProductRequestDTO));

        ResponseEntity<?> notExistsResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(notExistsResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(notExistsResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                + notExistsProductRequestDTO.getTitle() + "} não encontrado para o lojista passado.] ");
        Assertions.assertEquals(notExistsResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        final ProductRequestDTO nullStockProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), null);
        requestDTO.setProducts(List.of(nullStockProductRequestDTO));

        ResponseEntity<?> nullStockResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullStockResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullStockResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                + nullStockProductRequestDTO.getTitle() + "} passado com estoque inválido(vazio ou nulo).] ");
        Assertions.assertEquals(nullStockResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        final ProductRequestDTO negativeStockProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), -1);
        requestDTO.setProducts(List.of(negativeStockProductRequestDTO));

        ResponseEntity<?> negativeStockResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(negativeStockResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(negativeStockResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                + negativeStockProductRequestDTO.getTitle() + "} passado com estoque inválido(menor ou igual a 0).] ");
        Assertions.assertEquals(negativeStockResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        firstProductEntity.setStock(5);
        firstProductEntity.setPrice(9.99);
        firstProductEntity.setPricePromotion(9.99);
        firstProductEntity.setSellerId(sellerEntity.getId());
        firstProductEntity.setSellerName(sellerEntity.getName());
        productRepository.save(firstProductEntity);
        final ProductRequestDTO invalidStockProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), 10);
        requestDTO.setProducts(List.of(invalidStockProductRequestDTO));

        ResponseEntity<?> invalidStockResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(invalidStockResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(invalidStockResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                + invalidStockProductRequestDTO.getTitle() + "} não tem estoque necessário. Estoque solicitado: {" + invalidStockProductRequestDTO.getQuantity() +
                "}, estoque atual: {" + firstProductEntity.getStock() + "}] ");
        Assertions.assertEquals(invalidStockResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }
}
