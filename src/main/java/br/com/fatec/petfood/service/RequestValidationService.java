package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.generic.ProductRequest;

import java.util.List;

public interface RequestValidationService {

    void validateShippingPrice(Double shippingPrice) throws Exception;

    SellerEntity validateSellerRequestDTO(String sellerName) throws Exception;

    UserEntity validateUserRequestDTO(String userName) throws Exception;

    List<ProductRequest> validateProductsRequestDTO(List<ProductRequestDTO> products, String sellerName) throws Exception;

    void validateRequestEntityTotalValue(RequestEntity requestEntity) throws Exception;

    void validateFindRequestBySeller(String sellerName) throws Exception;

    void validateFindRequestByUser(String userName) throws Exception;
}
