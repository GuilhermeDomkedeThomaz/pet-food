package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import org.springframework.http.ResponseEntity;

public interface SearchService {

    ResponseEntity<?> searchSeller(String productTitle, CityZone cityZone, Boolean isWeek, String localTime, Integer page, Integer size);

    ResponseEntity<?> searchSellerProducts(String sellerName, String productTitle, Integer page, Integer size);

    ResponseEntity<?> searchSellerByCategory(Category category, CityZone cityZone, Boolean isWeek, String localTime, Integer page, Integer size);
}
