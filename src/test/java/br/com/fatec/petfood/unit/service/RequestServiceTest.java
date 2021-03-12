package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.dto.RequestReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.RequestMapper;
import br.com.fatec.petfood.repository.mongo.RequestRepository;
import br.com.fatec.petfood.service.impl.RequestServiceImpl;
import br.com.fatec.petfood.service.impl.RequestValidationServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import br.com.fatec.petfood.utils.ResponseHeadersUtils;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
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
    private RequestRepository requestRepository;

    @Mock
    private ResponseHeadersUtils responseHeadersUtils;

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
    private final ProductRequestDTO firstProductRequestDTO = EnhancedRandom.random(ProductRequestDTO.class);
    private final ProductRequestDTO secondProductRequestDTO = EnhancedRandom.random(ProductRequestDTO.class);
    private final List<ProductRequest> productRequestList = List.of(firstProductRequest, secondProductRequest);
    private final List<ProductRequestDTO> productRequestDTOList = List.of(firstProductRequestDTO, secondProductRequestDTO);

    @Test
    public void shouldCreateRequestWithSuccess() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        requestDTO.setProducts(productRequestDTOList);

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenReturn(productRequestList);
        Mockito.when(requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(), userEntity.getName(),
                productRequestList, requestDTO.getShippingPrice(), Status.CREATED)).thenReturn(requestEntity);
        Mockito.when(requestRepository.save(requestEntity)).thenReturn(requestEntity);

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Pedido registrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidShippingPrice() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        requestDTO.setShippingPrice(-9.99);

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.doThrow(new Exception()).when(requestValidationServiceImpl).validateShippingPrice(Mockito.anyDouble());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithNotExistsSeller() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenThrow(new Exception());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithNotExistsUser() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenThrow(new Exception());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestOnCreateRequestWithInvalidProducts() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenThrow(new Exception());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperWhenCreateRequest() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        requestDTO.setProducts(productRequestDTOList);

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestValidationServiceImpl.validateSellerRequestDTO(Mockito.anyString())).thenReturn(sellerEntity);
        Mockito.when(requestValidationServiceImpl.validateUserRequestDTO(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(requestValidationServiceImpl.validateProductsRequestDTO(eq(productRequestDTOList), Mockito.anyString()))
                .thenReturn(productRequestList);
        Mockito.when(requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(), userEntity.getName(),
                productRequestList, requestDTO.getShippingPrice(), Status.CREATED)).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.createRequest(requestDTO);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do pedido: null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithInvalidTotalValueOnMapperWhenCreateRequest() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        requestDTO.setProducts(productRequestDTOList);
        requestEntity.setTotalValue(-1000.99);

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
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
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseWhenCreateRequest() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        requestDTO.setProducts(productRequestDTOList);

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
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
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldFindBySellerWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenReturn(requestReturnDTO);

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), List.of(requestReturnDTO));
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindBySeller() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.doThrow(new Exception("Nome do lojista passado inválido(vazio ou nulo)."))
                .when(requestValidationServiceImpl).validateFindRequestBySeller(sellerEntity.getName());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindBySeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido(s) não encontrado(s) com o nome de lojista passado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.of(new ArrayList<>()));

        ResponseEntity<?> listResponse = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(listResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(listResponse.getBody(), "Pedido(s) não encontrado(s) com o nome de lojista passado.");
        Assertions.assertEquals(listResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnFindBySeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySeller(sellerEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para busca de pedido(s): null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldFindByUserWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenReturn(requestReturnDTO);

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), List.of(requestReturnDTO));
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindByUser() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.doThrow(new Exception("Nome do usuário passado inválido(vazio ou nulo)."))
                .when(requestValidationServiceImpl).validateFindRequestByUser(userEntity.getName());

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do usuário passado inválido(vazio ou nulo).");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindByUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Pedido(s) não encontrado(s) com o nome de usuário passado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.of(new ArrayList<>()));

        ResponseEntity<?> listResponse = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(listResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(listResponse.getBody(), "Pedido(s) não encontrado(s) com o nome de usuário passado.");
        Assertions.assertEquals(listResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnFindByUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllByUserName(Mockito.anyString())).thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.findRequestByUser(userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para busca de pedido(s): null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldFindBySellerAndUserWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenReturn(requestReturnDTO);

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), List.of(requestReturnDTO));
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWithInvalidParamsOnFindBySellerAndUser() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.doThrow(new Exception("Nome do lojista passado inválido(vazio ou nulo)."))
                .when(requestValidationServiceImpl).validateFindRequestBySeller(sellerEntity.getName());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindBySellerAndUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(),
                "Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(new ArrayList<>()));

        ResponseEntity<?> listResponse = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(listResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(listResponse.getBody(),
                "Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.");
        Assertions.assertEquals(listResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnFindBySellerAndUser() {HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(requestRepository.findAllBySellerNameAndUserName(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(List.of(requestEntity)));
        Mockito.when(requestMapper.toReturnDTO(eq(requestEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = requestServiceImpl.findRequestBySellerAndUser(sellerEntity.getName(), userEntity.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para busca de pedido(s): null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }
}
