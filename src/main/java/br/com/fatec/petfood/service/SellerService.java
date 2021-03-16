package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SellerService {

    ResponseEntity<?> createSeller(SellerDTO sellerDTO, CityZone cityZone, List<Category> categories);

    ResponseEntity<?> getSeller(String name);

    ResponseEntity<?> getSellerByEmail(String email);

    ResponseEntity<?> login(String email, String password);

    ResponseEntity<?> updateSeller(String document, SellerUpdateDTO sellerUpdateDTO, CityZone cityZone, List<Category> categories);

    ResponseEntity<?> deleteSeller(String name);
}
