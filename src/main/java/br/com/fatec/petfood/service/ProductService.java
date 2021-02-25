package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.enums.Category;
import org.springframework.http.ResponseEntity;

public interface ProductService {

    ResponseEntity<?> createProduct(ProductDTO productDTO, Category category);

    ResponseEntity<?> getProductByTitleAndSellerName(String title, String sellerName);

    ResponseEntity<?> updateProduct(String title, String sellerName, ProductUpdateDTO productUpdateDTO, Category category);

    ResponseEntity<?> deleteProduct(String title, String sellerName);
}
