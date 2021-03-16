package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;

import java.util.List;

public interface ValidationService {

    void validateUserDTO(UserDTO userDTO, CityZone cityZone) throws Exception;

    RegistrationInfos validateUserUpdateDTO(UserEntity userEntity, UserUpdateDTO userUpdateDTO, CityZone cityZone) throws Exception;

    void validateSellerDTO(SellerDTO sellerDTO, CityZone cityZone, List<Category> categories) throws Exception;

    RegistrationInfos validateSellerUpdateDTO(SellerEntity sellerEntity, SellerUpdateDTO sellerUpdateDTO, CityZone cityZone, List<Category> categories) throws Exception;

    SellerEntity validateProductDTO(ProductDTO productDTO, Category category) throws Exception;

    void validateProductUpdateDTO(ProductUpdateDTO productUpdateDTO, Category category) throws Exception;
}
