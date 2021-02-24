package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;

public interface ValidationService {

    void validateUserDTO(UserDTO userDTO) throws Exception;

    void validateSellerDTO(SellerDTO sellerDTO) throws Exception;

    SellerEntity validateProductDTO(ProductDTO productDTO) throws Exception;
}
