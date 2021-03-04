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
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(sellerResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProduct() {
        ResponseEntity<?> response = productService.createProduct(productDTOWithoutSellerName, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProductWithInvalidSellerName() {
        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado com o nome passado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProductAlreadyExists() {
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(sellerResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> productExistsResponse = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(productExistsResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(productExistsResponse.getBody(), "Título passado já cadastrado para o lojista passado.");
        Assertions.assertEquals(productExistsResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldFindProductWithSuccess() {
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(sellerResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> findResponse = productService.getProductByTitleAndSellerName(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(findResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindProduct() {
        ResponseEntity<?> findResponse = productService.getProductByTitleAndSellerName(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Produto não encontrado.");
        Assertions.assertEquals(findResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldUpdateProductWithSuccess() {
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(sellerResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> updateResponse = productService
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(updateResponse.getBody(), "Produto atualizado com sucesso.");
        Assertions.assertEquals(updateResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindProductToUpdate() {
        ResponseEntity<?> response = productService
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Produto não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldDeleteProductWithSuccess() {
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(sellerResponse.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> response = productService.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> deleteResponse = productService.deleteProduct(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(deleteResponse.getBody(), "Produto deletado com sucesso.");
        Assertions.assertEquals(deleteResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindProductForDelete() {
        ResponseEntity<?> findResponse = productService.deleteProduct(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Produto não encontrado.");
        Assertions.assertEquals(findResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }
}
