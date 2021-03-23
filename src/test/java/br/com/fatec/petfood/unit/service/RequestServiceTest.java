package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.dto.RequestReturnDTO;
import br.com.fatec.petfood.model.dto.RequestUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.RequestMapper;
import br.com.fatec.petfood.repository.mongo.RequestRepository;
import br.com.fatec.petfood.service.ProductService;
import br.com.fatec.petfood.service.impl.RequestServiceImpl;
import br.com.fatec.petfood.service.impl.RequestValidationServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

public class RequestServiceTest extends UnitTest {

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private ProductService productService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestValidationServiceImpl requestValidationServiceImpl;

    @InjectMocks
    private RequestServiceImpl requestServiceImpl;

    private final RequestDTO requestDTO = EnhancedRandom.random(RequestDTO.class);
    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final RequestEntity requestEntity = EnhancedRandom.random(RequestEntity.class);
    private final ProductRequest firstProductRequest = EnhancedRandom.random(ProductRequest.class);
    private final ProductRequest secondProductRequest = EnhancedRandom.random(ProductRequest.class);
    private final RequestReturnDTO requestReturnDTO = EnhancedRandom.random(RequestReturnDTO.class);
    private final RequestUpdateDTO requestUpdateDTO = EnhancedRandom.random(RequestUpdateDTO.class);
    private final ProductRequestDTO firstProductRequestDTO = EnhancedRandom.random(ProductRequestDTO.class);
    private final ProductRequestDTO secondProductRequestDTO = EnhancedRandom.random(ProductRequestDTO.class);
    private final List<ProductRequest> productRequestList = List.of(firstProductRequest, secondProductRequest);
    private final List<ProductRequestDTO> productRequestDTOList = List.of(firstProductRequestDTO, secondProductRequestDTO);

    @Test
    public void shouldCreateRequestWithSuccess() throws Exception {
        requestDTO.setProducts(productRequestDTOList);

        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenReturn(productRequestList);
        Mockito.when(requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(), userEntity.getName(),
                productRequestList, requestDTO.getShippingPrice(), Status.CREATED)).thenReturn(requestEntity);
        Mockito.when(requestRepository.save(requestEntity)).thenReturn(requestEntity);

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Pedido registrado com sucesso. Id do pedido: "
                + requestEntity.getId().toString());
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidShippingPrice() throws Exception {
        requestDTO.setShippingPrice(-9.99);

        Mockito.doThrow(new Exception()).when(requestValidationServiceImpl).validateShippingPrice(Mockito.anyDouble());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithNotExistsSeller() throws Exception {
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenThrow(new Exception());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithNotExistsUser() throws Exception {
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenThrow(new Exception());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidProducts() throws Exception {
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenThrow(new Exception());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperWhenCreateRequest() throws Exception {
        requestDTO.setProducts(productRequestDTOList);

        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenReturn(productRequestList);
        Mockito.when(requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(), userEntity.getName(),
                productRequestList, requestDTO.getShippingPrice(), Status.CREATED)).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do pedido: null");
    }

    @Test
    public void shouldResponseInternalServerErrorWithInvalidTotalValueOnMapperWhenCreateRequest() throws Exception {
        requestDTO.setProducts(productRequestDTOList);
        requestEntity.setTotalValue(-1000.99);

        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenReturn(productRequestList);
        Mockito.when(requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(), userEntity.getName(),
                productRequestList, requestDTO.getShippingPrice(), Status.CREATED)).thenReturn(requestEntity);
        Mockito.doThrow(new Exception("Erro no mapeamento para criação do pedido: Valor total inválido(menor ou igual a zero)."))
                .when(requestValidationServiceImpl).validateRequestEntityTotalValue(requestEntity);

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do pedido: Valor total inválido(menor ou igual a zero).");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseWhenCreateRequest() throws Exception {
        requestDTO.setProducts(productRequestDTOList);

        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenReturn(productRequestList);
        Mockito.when(requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(), userEntity.getName(),
                productRequestList, requestDTO.getShippingPrice(), Status.CREATED)).thenReturn(requestEntity);
        Mockito.when(requestRepository.save(requestEntity)).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao gravar pedido na base de dados: ");
    }

    @Test
    public void shouldFindByIdWithSuccess() {
        Mockito.when(requestRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenReturn(requestReturnDTO);

        ResponseEntity<?> response = requestServiceImpl.findRequestById(requestEntity.getId().toString());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), requestReturnDTO);
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindById() throws Exception {
        Mockito.doThrow(new Exception("Id do pedido passado inválido(vazio ou nulo)."))
                .when(requestValidationServiceImpl).validateFindRequestById(requestEntity.getId().toString());

        ResponseEntity<?> response = requestServiceImpl.findRequestById(requestEntity.getId().toString());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Id do pedido passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldNotFindById() {
        Mockito.when(requestRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.findRequestById(requestEntity.getId().toString());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnFindById() {
        Mockito.when(requestRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.findRequestById(requestEntity.getId().toString());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para busca de pedido: null");
    }

    @Test
    public void shouldFindBySellerWithSuccess() {
        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenReturn(requestReturnDTO);

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), List.of(requestReturnDTO));
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindBySeller() throws Exception {
        Mockito.doThrow(new Exception("Nome do lojista passado inválido(vazio ou nulo)."))
                .when(requestValidationServiceImpl).validateFindRequestBySeller(sellerEntity.getName());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldNotFindBySeller() {
        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido(s) não encontrado(s) com o nome de lojista passado.");

        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.of(new ArrayList<>()));

        ResponseEntity<?> listResponse = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(listResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(listResponse.getBody(), "Pedido(s) não encontrado(s) com o nome de lojista passado.");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnFindBySeller() {
        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para busca de pedido(s): null");
    }

    @Test
    public void shouldFindByUserWithSuccess() {
        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenReturn(requestReturnDTO);

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), List.of(requestReturnDTO));
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindByUser() throws Exception {
        Mockito.doThrow(new Exception("Nome do usuário passado inválido(vazio ou nulo)."))
                .when(requestValidationServiceImpl).validateFindRequestByUser(userEntity.getName());

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldNotFindByUser() {
        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido(s) não encontrado(s) com o nome de usuário passado.");

        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.of(new ArrayList<>()));

        ResponseEntity<?> listResponse = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(listResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(listResponse.getBody(), "Pedido(s) não encontrado(s) com o nome de usuário passado.");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnFindByUser() {
        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para busca de pedido(s): null");
    }

    @Test
    public void shouldFindBySellerAndUserWithSuccess() {
        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenReturn(requestReturnDTO);

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), List.of(requestReturnDTO));
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindBySellerAndUser() throws Exception {
        Mockito.doThrow(new Exception("Nome do lojista passado inválido(vazio ou nulo)."))
                .when(requestValidationServiceImpl).validateFindRequestBySeller(sellerEntity.getName());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldNotFindBySellerAndUser() {
        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(),
                "Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.");

        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(new ArrayList<>()));

        ResponseEntity<?> listResponse = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(listResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(listResponse.getBody(),
                "Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnFindBySellerAndUser() {
        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para busca de pedido(s): null");
    }

    @Test
    public void shouldUpdateRequestWithSuccess() throws Exception {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestValidationServiceImpl.validateProductsRequestUpdateDTO(eq(requestEntity), eq(requestUpdateDTO)))
                .thenReturn(productRequestList);
        Mockito.when(requestValidationServiceImpl.validateShippingPriceRequestUpdateDTO(eq(requestEntity), eq(requestUpdateDTO)))
                .thenReturn(5.99);
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(productRequestList), eq(5.99), eq(Status.PROCESSED)))
                .thenReturn(requestEntity);
        Mockito.when(requestRepository.save(eq(requestEntity))).thenReturn(requestEntity);

        ResponseEntity<?> response = requestServiceImpl.updateRequest(objectId.toString(), Status.PROCESSED, requestUpdateDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Pedido atualizado com sucesso.");
    }

    @Test
    public void shouldNotFindRequestToUpdate() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.updateRequest(objectId.toString(), Status.PROCESSED, requestUpdateDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldResponseBadRequestOnValidateProductsRequestUpdate() throws Exception {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestValidationServiceImpl.validateProductsRequestUpdateDTO(eq(requestEntity), eq(requestUpdateDTO)))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.updateRequest(objectId.toString(), Status.PROCESSED, requestUpdateDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldResponseInternalServerErrorOnMapperRequestUpdate() throws Exception {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestValidationServiceImpl.validateProductsRequestUpdateDTO(eq(requestEntity), eq(requestUpdateDTO)))
                .thenReturn(productRequestList);
        Mockito.when(requestValidationServiceImpl.validateShippingPriceRequestUpdateDTO(eq(requestEntity), eq(requestUpdateDTO)))
                .thenReturn(5.99);
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(productRequestList), eq(5.99), eq(Status.PROCESSED)))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.updateRequest(objectId.toString(), Status.PROCESSED, requestUpdateDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para atualização de pedido: null");
    }

    @Test
    public void shouldResponseInternalServerErrorOnDataBaseRequestUpdate() throws Exception {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestValidationServiceImpl.validateProductsRequestUpdateDTO(eq(requestEntity), eq(requestUpdateDTO)))
                .thenReturn(productRequestList);
        Mockito.when(requestValidationServiceImpl.validateShippingPriceRequestUpdateDTO(eq(requestEntity), eq(requestUpdateDTO)))
                .thenReturn(5.99);
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(productRequestList), eq(5.99), eq(Status.PROCESSED)))
                .thenReturn(requestEntity);
        Mockito.when(requestRepository.save(eq(requestEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = requestServiceImpl.updateRequest(objectId.toString(), Status.PROCESSED, requestUpdateDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao atualizar pedido na base de dados: ");
    }

    @Test
    public void shouldUpdateStatusRequestWithSuccess() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(Status.PROCESSED))).thenReturn(requestEntity);
        Mockito.when(requestRepository.save(eq(requestEntity))).thenReturn(requestEntity);

        ResponseEntity<?> response = requestServiceImpl.updateStatusRequest(objectId.toString(), Status.PROCESSED);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Status do pedido atualizado com sucesso.");
    }

    @Test
    public void shouldNotFindRequestToUpdateStatus() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.updateStatusRequest(objectId.toString(), Status.PROCESSED);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldResponseBadRequestOnValidateProductsRequestUpdateStatus() {
        ResponseEntity<?> response = requestServiceImpl.updateStatusRequest("1", Status.PROCESSED);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Id do pedido passado inválido.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnMapperRequestUpdateStatus() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(Status.PROCESSED))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.updateStatusRequest(objectId.toString(), Status.PROCESSED);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para atualização de status do pedido: null");
    }

    @Test
    public void shouldResponseInternalServerErrorOnDataBaseRequestUpdateStatus() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(Status.PROCESSED))).thenReturn(requestEntity);
        Mockito.when(requestRepository.save(eq(requestEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = requestServiceImpl.updateStatusRequest(objectId.toString(), Status.PROCESSED);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao atualizar status do pedido na base de dados: ");
    }

    @Test
    public void shouldRateRequestWithSuccess() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(10))).thenReturn(requestEntity);
        Mockito.when(requestRepository.save(eq(requestEntity))).thenReturn(requestEntity);

        ResponseEntity<?> response = requestServiceImpl.rateRequest(objectId.toString(), 10);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Pedido avaliado com sucesso.");
    }

    @Test
    public void shouldNotFindRequestToRateRequest() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.rateRequest(objectId.toString(), 10);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldResponseBadRequestOnValidateProductsRateRequest() {
        ResponseEntity<?> response = requestServiceImpl.rateRequest("1", 10);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Id do pedido passado inválido.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnMapperRateRequest() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(10))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.rateRequest(objectId.toString(), 10);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para avaliação do pedido: null");
    }

    @Test
    public void shouldResponseInternalServerErrorOnDataBaseRateRequest() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));
        Mockito.when(requestMapper.toEntity(eq(requestEntity), eq(10))).thenReturn(requestEntity);
        Mockito.when(requestRepository.save(eq(requestEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = requestServiceImpl.rateRequest(objectId.toString(), 10);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao avaliar pedido na base de dados: ");
    }

    @Test
    public void shouldDeleteRequestWithSuccess() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.of(requestEntity));

        ResponseEntity<?> response = requestServiceImpl.deleteRequest(objectId.toString());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Pedido deletado com sucesso.");
    }

    @Test
    public void shouldNotFindRequestForDelete() {
        ObjectId objectId = new ObjectId();

        Mockito.when(requestRepository.findById(eq(objectId))).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.deleteRequest(objectId.toString());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido não encontrado com o id de pedido passado.");
    }

    @Test
    public void shouldResponseBadRequestWithInvalidIdOnDeleteRequest() {
        ResponseEntity<?> response = requestServiceImpl.deleteRequest("1");

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Id do pedido passado inválido.");
    }
}
