package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.enums.Pets;
import org.springframework.http.ResponseEntity;

public interface ProductService {

    ResponseEntity<?> createProduct(ProductDTO productDTO, Pets pets);

    ResponseEntity<?> getProductByTitle(String title, String sellerName);
}
