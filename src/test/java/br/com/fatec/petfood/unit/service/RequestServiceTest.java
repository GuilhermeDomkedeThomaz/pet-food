package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.dto.RequestDTO;
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

import java.util.List;

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
}
