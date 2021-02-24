package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import br.com.fatec.petfood.service.ProductService;
import br.com.fatec.petfood.service.SellerService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ProductServiceTest extends IntegrationTest {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private ProductService productService;

    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final ProductDTO productDTO = EnhancedRandom.random(ProductDTO.class);
    private final ProductDTO productDTOWithoutSellerName = EnhancedRandom.random(ProductDTO.class, "sellerName");

    @Test
    public void shouldCreateProductWithSuccess() {
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Pets.DOG);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProduct() {
        ResponseEntity<?> response = productService.createProduct(productDTOWithoutSellerName, Pets.DOG);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProductWithInvalidSellerName() {
        ResponseEntity<?> response = productService.createProduct(productDTO, Pets.DOG);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado com o nome passado.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProductAlreadyExists() {
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Pets.DOG);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");

        ResponseEntity<?> productExistsResponse = productService.createProduct(productDTO, Pets.DOG);

        Assertions.assertEquals(productExistsResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(productExistsResponse.getBody(), "Título passado já cadastrado para o lojista passado.");
    }

    @Test
    public void shouldFindProductWithSuccess() {
        productDTO.setSellerName(sellerDTO.getName());

        ResponseEntity<?> sellerResponse = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(sellerResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(sellerResponse.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> response = productService.createProduct(productDTO, Pets.DOG);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");

        ResponseEntity<?> findResponse = productService.getProductByTitle(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldNotFindProduct() {
        ResponseEntity<?> findResponse = productService.getProductByTitle(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Produto não encontrado.");
    }
}
