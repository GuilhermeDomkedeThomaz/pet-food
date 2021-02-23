package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.UserDTO;

public interface ValidationService {

    void validateUserDTO(UserDTO userDTO) throws Exception;

    void validateSellerDTO(SellerDTO sellerDTO) throws Exception;
}
