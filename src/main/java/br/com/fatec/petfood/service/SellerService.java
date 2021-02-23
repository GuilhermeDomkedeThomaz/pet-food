package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import org.springframework.http.ResponseEntity;

public interface SellerService {

    ResponseEntity<?> createSeller(SellerDTO sellerDTO, CityZone cityZone);

    ResponseEntity<?> getSeller(String name);

    ResponseEntity<?> login(String email, String password);
}
