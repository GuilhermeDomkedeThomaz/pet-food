package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.dto.RequestUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.RequestRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.RequestService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestServiceTest extends IntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProductRepository productRepository;

    private final RequestDTO requestDTO = EnhancedRandom.random(RequestDTO.class);
    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity firstProductEntity = EnhancedRandom.random(ProductEntity.class);
    private final ProductEntity secondProductEntity = EnhancedRandom.random(ProductEntity.class);
    private final RequestUpdateDTO requestUpdateDTO = EnhancedRandom.random(RequestUpdateDTO.class);

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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(response.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());
        }

        productRepository.findById(firstProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

        productRepository.findById(secondProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidShippingPrice() {
        requestDTO.setShippingPrice(null);

        ResponseEntity<?> nullResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullResponse.getBody(), "Valor de frete passado inválido(vazio ou nulo).");

        requestDTO.setShippingPrice(-9.99);

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Valor de frete passado inválido(menor que zero).");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidSeller() {
        requestDTO.setSellerName(null);

        ResponseEntity<?> nullResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullResponse.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");

        requestDTO.setSellerName(sellerEntity.getName());

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado com o nome passado.");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidUser() {
        sellerRepository.save(sellerEntity);
        requestDTO.setSellerName(sellerEntity.getName());

        requestDTO.setUserName(null);

        ResponseEntity<?> nullResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullResponse.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");

        requestDTO.setUserName(userEntity.getName());

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado com o nome passado.");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidProducts() {
        userRepository.save(userEntity);
        sellerRepository.save(sellerEntity);
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(null);

        ResponseEntity<?> nullResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).");

        requestDTO.setProducts(new ArrayList<>());

        ResponseEntity<?> emptyResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(emptyResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(emptyResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).");

        final ProductRequestDTO notExistsProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), 1);
        requestDTO.setProducts(List.of(notExistsProductRequestDTO));

        ResponseEntity<?> notExistsResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(notExistsResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(notExistsResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                + notExistsProductRequestDTO.getTitle() + "} não encontrado para o lojista passado.] ");

        final ProductRequestDTO nullStockProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), null);
        requestDTO.setProducts(List.of(nullStockProductRequestDTO));

        ResponseEntity<?> nullStockResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(nullStockResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullStockResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                + nullStockProductRequestDTO.getTitle() + "} passado com estoque inválido(vazio ou nulo).] ");

        final ProductRequestDTO negativeStockProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), -1);
        requestDTO.setProducts(List.of(negativeStockProductRequestDTO));

        ResponseEntity<?> negativeStockResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(negativeStockResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(negativeStockResponse.getBody(), "Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido):  [Produto com o título: {"
                + negativeStockProductRequestDTO.getTitle() + "} passado com estoque inválido(menor ou igual a 0).] ");

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
    }

    @Test
    public void shouldFindWithSuccess() {
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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));
        requestService.createRequest(requestDTO);

        ResponseEntity<?> sellerResponse = requestService.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.OK);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            ResponseEntity<?> idResponse = requestService.findRequestById(optionalRequestEntityList.get().get(0).getId().toString());

            Assertions.assertEquals(idResponse.getStatusCode(), HttpStatus.OK);
        }

        ResponseEntity<?> userResponse = requestService.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(userResponse.getStatusCode(), HttpStatus.OK);

        ResponseEntity<?> sellerAndUserResponse =
                requestService.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(sellerAndUserResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindByIdOrSellerOrUser() {
        ResponseEntity<?> nullIdResponse = requestService.findRequestById(null);

        Assertions.assertEquals(nullIdResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullIdResponse.getBody(), "Id do pedido passado inválido(vazio ou nulo).");

        ResponseEntity<?> emptyIdResponse = requestService.findRequestById("");

        Assertions.assertEquals(emptyIdResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(emptyIdResponse.getBody(), "Id do pedido passado inválido(vazio ou nulo).");

        ResponseEntity<?> invalidIdResponse = requestService.findRequestById("1");

        Assertions.assertEquals(invalidIdResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(invalidIdResponse.getBody(), "Id do pedido passado inválido.");

        ResponseEntity<?> nullSellerResponse = requestService.findRequestBySeller(null);

        Assertions.assertEquals(nullSellerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullSellerResponse.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");

        ResponseEntity<?> emptySellerResponse = requestService.findRequestBySeller("");

        Assertions.assertEquals(emptySellerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(emptySellerResponse.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");

        ResponseEntity<?> nullUserResponse = requestService.findRequestByUser(null);

        Assertions.assertEquals(nullUserResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullUserResponse.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");

        ResponseEntity<?> emptyUserResponse = requestService.findRequestByUser("");

        Assertions.assertEquals(emptyUserResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(emptyUserResponse.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindBySellerAndUser() {
        ResponseEntity<?> nullSellerResponse =
                requestService.findRequestBySellerAndUser(null, userEntity.getName());

        Assertions.assertEquals(nullSellerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullSellerResponse.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");

        ResponseEntity<?> emptySellerResponse =
                requestService.findRequestBySellerAndUser("", userEntity.getName());

        Assertions.assertEquals(emptySellerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(emptySellerResponse.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");

        ResponseEntity<?> nullUserResponse =
                requestService.findRequestBySellerAndUser(sellerEntity.getName(), null);

        Assertions.assertEquals(nullUserResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(nullUserResponse.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");

        ResponseEntity<?> emptyUserResponse =
                requestService.findRequestBySellerAndUser(sellerEntity.getName(), "");

        Assertions.assertEquals(emptyUserResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(emptyUserResponse.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldNotFind() {
        ResponseEntity<?> idResponse = requestService.findRequestById(sellerEntity.getId().toString());

        Assertions.assertEquals(idResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(idResponse.getBody(), "Pedido não encontrado com o id de pedido passado.");

        ResponseEntity<?> sellerResponse = requestService.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(sellerResponse.getBody(), "Pedido(s) não encontrado(s) com o nome de lojista passado.");

        ResponseEntity<?> userResponse = requestService.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(userResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(userResponse.getBody(), "Pedido(s) não encontrado(s) com o nome de usuário passado.");

        ResponseEntity<?> sellerAndUserResponse =
                requestService.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(sellerAndUserResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(sellerAndUserResponse.getBody(),
                "Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.");
    }

    @Test
    public void shouldUpdateRequestWithSuccess() {
        ObjectId objectId;
        userRepository.save(userEntity);
        sellerRepository.save(sellerEntity);
        firstProductEntity.setStock(10);
        firstProductEntity.setPrice(9.99);
        firstProductEntity.setPricePromotion(9.99);
        firstProductEntity.setSellerId(sellerEntity.getId());
        firstProductEntity.setSellerName(sellerEntity.getName());
        secondProductEntity.setStock(10);
        secondProductEntity.setPrice(9.99);
        secondProductEntity.setPricePromotion(9.99);
        secondProductEntity.setSellerId(sellerEntity.getId());
        secondProductEntity.setSellerName(sellerEntity.getName());
        productRepository.save(firstProductEntity);
        productRepository.save(secondProductEntity);
        final ProductRequestDTO firstProductRequestDTO = new ProductRequestDTO(firstProductEntity.getTitle(), 1);
        final ProductRequestDTO secondProductRequestDTO = new ProductRequestDTO(secondProductEntity.getTitle(), 1);
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> createResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(createResponse.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(createResponse.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());
            objectId = requestEntity.getId();

            firstProductRequestDTO.setQuantity(5);
            secondProductRequestDTO.setQuantity(5);
            requestUpdateDTO.setShippingPrice(0.00);
            requestUpdateDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

            ResponseEntity<?> updateResponse = requestService.updateRequest(objectId.toString(), Status.PROCESSED, requestUpdateDTO);

            Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(updateResponse.getBody(), "Pedido atualizado com sucesso.");
        }

        optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(requestEntity.getProducts().get(0).getQuantity(), 5);
            Assertions.assertEquals(requestEntity.getProducts().get(1).getQuantity(), 5);
            Assertions.assertEquals(requestEntity.getShippingPrice(), 0.00);
            Assertions.assertEquals(requestEntity.getStatus(), Status.PROCESSED);
        }
    }

    @Test
    public void shouldUpdateOnlyStatusRequestWithSuccess() {
        ObjectId objectId;
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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> createResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(createResponse.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(createResponse.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());
            objectId = requestEntity.getId();

            requestUpdateDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

            ResponseEntity<?> updateResponse = requestService.updateRequest(objectId.toString(), Status.PROCESSED, requestUpdateDTO);

            Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(updateResponse.getBody(), "Pedido atualizado com sucesso.");
        }

        optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(requestEntity.getProducts().get(0).getQuantity(), 1);
            Assertions.assertEquals(requestEntity.getProducts().get(1).getQuantity(), 1);
            Assertions.assertEquals(requestEntity.getStatus(), Status.PROCESSED);
        }
    }

    @Test
    public void shouldResponseBadRequestWithInvalidIdOnRequestUpdate() {
        ResponseEntity<?> updateResponse = requestService.updateRequest("1", Status.PROCESSED, requestUpdateDTO);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Id do pedido passado inválido.");
    }

    @Test
    public void shouldNoFindRequestToUpdate() {
        ResponseEntity<?> updateResponse =
                requestService.updateRequest(new ObjectId().toString(), Status.PROCESSED, requestUpdateDTO);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldUpdateStatusRequestWithSuccess() {
        ObjectId objectId;
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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> createResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(createResponse.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(createResponse.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());
            objectId = requestEntity.getId();

            ResponseEntity<?> updateResponse = requestService.updateStatusRequest(objectId.toString(), Status.PROCESSED);

            Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(updateResponse.getBody(), "Status do pedido atualizado com sucesso.");
        }

        optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(requestEntity.getStatus(), Status.PROCESSED);
        }
    }

    @Test
    public void shouldUpdateStatusRequestToCanceledWithSuccess() {
        ObjectId objectId;
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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> createResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(createResponse.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(createResponse.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());
            objectId = requestEntity.getId();

            productRepository.findById(firstProductEntity.getId().toString())
                    .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

            productRepository.findById(secondProductEntity.getId().toString())
                    .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

            ResponseEntity<?> updateResponse = requestService.updateStatusRequest(objectId.toString(), Status.CANCELED);

            Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(updateResponse.getBody(), "Status do pedido atualizado com sucesso.");
        }

        optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(requestEntity.getStatus(), Status.CANCELED);
        }

        productRepository.findById(firstProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 5));

        productRepository.findById(secondProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 5));
    }

    @Test
    public void shouldResponseBadRequestWithInvalidIdOnRequestStatusUpdate() {
        ResponseEntity<?> updateResponse = requestService.updateStatusRequest("1", Status.PROCESSED);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Id do pedido passado inválido.");
    }

    @Test
    public void shouldNoFindRequestToUpdateStatus() {
        ResponseEntity<?> updateResponse = requestService.updateStatusRequest(new ObjectId().toString(), Status.PROCESSED);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldRateRequestWithSuccess() {
        ObjectId objectId;
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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> createResponse = requestService.createRequest(requestDTO);

        Assertions.assertEquals(createResponse.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(createResponse.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());
            objectId = requestEntity.getId();

            ResponseEntity<?> updateResponse = requestService.rateRequest(objectId.toString(), 10);

            Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(updateResponse.getBody(), "Pedido avaliado com sucesso.");
        }

        optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(requestEntity.getRate(), 10);
        }
    }

    @Test
    public void shouldResponseBadRequestWithInvalidIdOnRateRequest() {
        ResponseEntity<?> updateResponse = requestService.rateRequest("1", 10);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Id do pedido passado inválido.");
    }

    @Test
    public void shouldNoFindRequestToRateRequest() {
        ResponseEntity<?> updateResponse = requestService.rateRequest(new ObjectId().toString(), 10);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldDeleteRequestWithSuccess() {
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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(response.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());

            productRepository.findById(firstProductEntity.getId().toString())
                    .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

            productRepository.findById(secondProductEntity.getId().toString())
                    .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

            ResponseEntity<?> deleteResponse = requestService.deleteRequest(requestEntity.getId().toString());

            Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(deleteResponse.getBody(), "Pedido deletado com sucesso.");
        }

        productRepository.findById(firstProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 5));

        productRepository.findById(secondProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 5));
    }

    @Test
    public void shouldDeleteCanceledRequestWithSuccess() {
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
        requestDTO.setShippingPrice(9.99);
        requestDTO.setUserName(userEntity.getName());
        requestDTO.setSellerName(sellerEntity.getName());
        requestDTO.setProducts(List.of(firstProductRequestDTO, secondProductRequestDTO));

        ResponseEntity<?> response = requestService.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerEntity.getName());

        if (optionalRequestEntityList.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntityList.get().get(0);
            Assertions.assertEquals(response.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                    + requestEntity.getId().toString());

            productRepository.findById(firstProductEntity.getId().toString())
                    .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

            productRepository.findById(secondProductEntity.getId().toString())
                    .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

            requestEntity.setStatus(Status.CANCELED);
            requestRepository.save(requestEntity);

            ResponseEntity<?> deleteResponse = requestService.deleteRequest(requestEntity.getId().toString());

            Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(deleteResponse.getBody(), "Pedido deletado com sucesso.");
        }

        productRepository.findById(firstProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));

        productRepository.findById(secondProductEntity.getId().toString())
                .ifPresent(productEntity -> Assertions.assertEquals(productEntity.getStock(), 4));
    }

    @Test
    public void shouldNotFindRequestForDelete() {
        ResponseEntity<?> deleteResponse = requestService.deleteRequest(new ObjectId().toString());

        Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(deleteResponse.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldResponseBadRequestWithInvalidIdOnDeleteRequest() {
        ResponseEntity<?> deleteResponse = requestService.deleteRequest("1");

        Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(deleteResponse.getBody(), "Id do pedido passado inválido.");
    }
}
