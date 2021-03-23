package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.service.ProductService;
import br.com.fatec.petfood.service.SellerService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

public class ProductServiceTest extends IntegrationTest {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private ProductService productService;

    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final ProductDTO productDTO = EnhancedRandom.random(ProductDTO.class);
    private final List<Category> categories = Arrays.asList(Category.FOOD, Category.OTHERS);
    private final ProductUpdateDTO productUpdateDTO = EnhancedRandom.random(ProductUpdateDTO.class);
    private final ProductDTO productDTOWithoutSellerName = EnhancedRandom.random(ProductDTO.class, "sellerName");

    @Test
    public void shouldCreateProductWithSuccess() {
        productDTO.setStock(5);
        productDTO.setPrice(9.99);
        productDTO.setPricePromotion(9.99);
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProduct() {
        ResponseEntity<?> response = productService.createProduct(productDTOWithoutSellerName, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProductWithInvalidSellerName() {
        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado com o nome passado.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProductAlreadyExists() {
        productDTO.setStock(5);
        productDTO.setPrice(9.99);
        productDTO.setPricePromotion(9.99);
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");

        ResponseEntity<?> productExistsResponse = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(productExistsResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(productExistsResponse.getBody(), "Título passado já cadastrado para o lojista passado.");
    }

    @Test
    public void shouldFindProductWithSuccess() {
        productDTO.setStock(5);
        productDTO.setPrice(9.99);
        productDTO.setPricePromotion(9.99);
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");

        ResponseEntity<?> findResponse = productService.getProductByTitleAndSellerName(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldNotFindProduct() {
        ResponseEntity<?> findResponse = productService.getProductByTitleAndSellerName(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Produto não encontrado.");
    }

    @Test
    public void shouldUpdateProductWithSuccess() {
        productDTO.setStock(5);
        productDTO.setPrice(9.99);
        productDTO.setPricePromotion(9.99);
        productDTO.setSellerName(sellerDTO.getName());
        productUpdateDTO.setStock(10);
        productUpdateDTO.setPrice(99.99);
        productUpdateDTO.setPricePromotion(99.99);

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");

        ResponseEntity<?> updateResponse = productService
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(updateResponse.getBody(), "Produto atualizado com sucesso.");
    }

    @Test
    public void shouldNotFindProductToUpdate() {
        ResponseEntity<?> response = productService
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Produto não encontrado.");
    }

    @Test
    public void shouldDeleteProductWithSuccess() {
        productDTO.setStock(5);
        productDTO.setPrice(9.99);
        productDTO.setPricePromotion(9.99);
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");

        ResponseEntity<?> deleteResponse = productService.deleteProduct(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(deleteResponse.getBody(), "Produto deletado com sucesso.");
    }

    @Test
    public void shouldNotFindProductForDelete() {
        ResponseEntity<?> findResponse = productService.deleteProduct(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Produto não encontrado.");
    }

    @Test
    public void shouldUpdateStockProductWithSuccess() {
        productDTO.setStock(5);
        productDTO.setPrice(9.99);
        productDTO.setPricePromotion(9.99);
        productDTO.setSellerName(sellerDTO.getName());
        productUpdateDTO.setStock(10);
        productUpdateDTO.setPrice(99.99);
        productUpdateDTO.setPricePromotion(99.99);

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");

        ResponseEntity<?> updateResponse = productService
                .updateStockProduct(productDTO.getTitle(), productDTO.getSellerName(), 50);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(updateResponse.getBody(), "Estoque do produto atualizado com sucesso.");
    }

    @Test
    public void shouldNotFindProductToUpdateStock() {
        ResponseEntity<?> response = productService
                .updateStockProduct(productDTO.getTitle(), productDTO.getSellerName(), 10);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Produto não encontrado.");
    }
}
