package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.enums.Category;
import org.springframework.http.ResponseEntity;

public interface SearchService {

    ResponseEntity<?> searchSeller(String productTitle, Boolean isWeek, String localTime);

    ResponseEntity<?> searchSellerProducts(String sellerName, String productTitle);

    ResponseEntity<?> searchSellerByCategory(Category category, Boolean isWeek, String localTime);
}
