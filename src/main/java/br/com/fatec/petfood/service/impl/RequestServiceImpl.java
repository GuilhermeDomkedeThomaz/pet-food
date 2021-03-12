package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.dto.RequestReturnDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            requestValidationService.validateShippingPrice(requestDTO.getShippingPrice());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

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
                requestValidationService.validateRequestEntityTotalValue(requestEntity);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }

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

    @Override
    public ResponseEntity<?> findRequestBySeller(String sellerName) {
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();

        try {
            requestValidationService.validateFindRequestBySeller(sellerName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerName);

        if (optionalRequestEntityList.isPresent()) {
            List<RequestEntity> requestEntityList = optionalRequestEntityList.get();

            if (!requestEntityList.isEmpty())
                return this.findReturn(requestEntityList, responseHeaders);
            else
                return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de lojista passado.",
                        responseHeaders, HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de lojista passado.",
                    responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> findRequestByUser(String userName) {
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();

        try {
            requestValidationService.validateFindRequestByUser(userName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllByUserName(userName);

        if (optionalRequestEntityList.isPresent()) {
            List<RequestEntity> requestEntityList = optionalRequestEntityList.get();

            if (!requestEntityList.isEmpty())
                return this.findReturn(requestEntityList, responseHeaders);
            else
                return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de usuário passado.",
                        responseHeaders, HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de usuário passado.",
                    responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> findRequestBySellerAndUser(String sellerName, String userName) {
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();

        try {
            requestValidationService.validateFindRequestBySeller(sellerName);
            requestValidationService.validateFindRequestByUser(userName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerNameAndUserName(sellerName, userName);

        if (optionalRequestEntityList.isPresent()) {
            List<RequestEntity> requestEntityList = optionalRequestEntityList.get();

            if (!requestEntityList.isEmpty())
                return this.findReturn(requestEntityList, responseHeaders);
            else
                return new ResponseEntity<>("Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.",
                        responseHeaders, HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.",
                    responseHeaders, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> findReturn(List<RequestEntity> requestEntityList, HttpHeaders responseHeaders) {
        List<RequestReturnDTO> requestReturnDTOList = new ArrayList<>();

        try {
            requestEntityList.forEach(requestEntity -> requestReturnDTOList.add(requestMapper.toReturnDTO(requestEntity)));
            return new ResponseEntity<>(requestReturnDTOList, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro no mapeamento para busca de pedido(s): " + e.getMessage(),
                    responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}