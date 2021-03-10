package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.RequestMapper;
import br.com.fatec.petfood.repository.mongo.RequestRepository;
import br.com.fatec.petfood.service.RequestService;
import br.com.fatec.petfood.service.RequestValidationService;
import br.com.fatec.petfood.utils.ResponseHeadersUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final ResponseHeadersUtils responseHeadersUtils;
    private final RequestValidationService requestValidationService;

    @Override
    public ResponseEntity<?> createRequest(RequestDTO requestDTO) {
        UserEntity userEntity;
        SellerEntity sellerEntity;
        List<ProductRequest> productRequests;
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();

        try {
            sellerEntity = requestValidationService.validateSellerRequestDTO(requestDTO.getSellerName());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {
            userEntity = requestValidationService.validateUserRequestDTO(requestDTO.getUserName());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {
            productRequests = requestValidationService.validateProductsRequestDTO(requestDTO.getProducts(), requestDTO.getSellerName());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {
            RequestEntity requestEntity = requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(),
                    userEntity.getName(), productRequests, requestDTO.getShippingPrice(), Status.CREATED);

            try {
                requestRepository.save(requestEntity);
                return new ResponseEntity<>("Pedido registrado com sucesso.", responseHeaders, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao gravar pedido na base de dados: " + e.getMessage(),
                        responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Erro no mapeamento para criação do pedido: " + e.getMessage(),
                    responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
