package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.service.ValidationService;
import br.com.fatec.petfood.service.impl.ProductServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

public class ProductServiceTest extends UnitTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private final ProductDTO productDTO = EnhancedRandom.random(ProductDTO.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity productEntity = EnhancedRandom.random(ProductEntity.class);
    private final ProductReturnDTO productReturnDTO = EnhancedRandom.random(ProductReturnDTO.class);
    private final ProductUpdateDTO productUpdateDTO = EnhancedRandom.random(ProductUpdateDTO.class);

    @Test
    public void shouldCreateProductWithSuccess() throws Exception {
        Mockito.when(validationService.validateProductDTO(eq(productDTO), eq(Category.FOOD))).thenReturn(sellerEntity);
        Mockito.when(productMapper.toEntity(eq(productDTO), eq(sellerEntity.getId()), eq(sellerEntity.getName()), eq(Category.FOOD)))
                .thenReturn(productEntity);
        Mockito.when(productRepository.save(eq(productEntity))).thenReturn(productEntity);

        ResponseEntity<?> response = productServiceImpl.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Produto cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateProduct() throws Exception {
        Mockito.doThrow(new Exception("Nome do lojista passado inválido(vazio ou nulo)."))
                .when(validationService).validateProductDTO(productDTO, Category.FOOD);

        ResponseEntity<?> response = productServiceImpl.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperWhenCreateProduct() throws Exception {
        Mockito.when(validationService.validateProductDTO(eq(productDTO), eq(Category.FOOD))).thenReturn(sellerEntity);
        Mockito.when(productMapper.toEntity(eq(productDTO), eq(sellerEntity.getId()), eq(sellerEntity.getName()), eq(Category.FOOD)))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = productServiceImpl.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do produto: null");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseWhenCreateProduct() throws Exception {
        Mockito.when(validationService.validateProductDTO(eq(productDTO), eq(Category.FOOD))).thenReturn(sellerEntity);
        Mockito.when(productMapper.toEntity(eq(productDTO), eq(sellerEntity.getId()), eq(sellerEntity.getName()), eq(Category.FOOD)))
                .thenReturn(productEntity);
        Mockito.when(productRepository.save(eq(productEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = productServiceImpl.createProduct(productDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao gravar produto na base de dados: ");
    }

    @Test
    public void shouldFindProductWithSuccess() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.of(productEntity));
        Mockito.when(productMapper.toReturnDTO(eq(productEntity))).thenReturn(productReturnDTO);

        ResponseEntity<?> response = productServiceImpl.getProductByTitleAndSellerName(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), productReturnDTO);
    }

    @Test
    public void shouldNotFindProduct() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = productServiceImpl.getProductByTitleAndSellerName(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Produto não encontrado.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnFindProduct() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.of(productEntity));
        Mockito.when(productMapper.toReturnDTO(eq(productEntity))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = productServiceImpl.getProductByTitleAndSellerName(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do produto: null");
    }

    @Test
    public void shouldUpdateProductWithSuccess() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.of(productEntity));
        Mockito.when(productMapper.toEntity(eq(productUpdateDTO), eq(productEntity), eq(Category.FOOD))).thenReturn(productEntity);
        Mockito.when(productRepository.save(eq(productEntity))).thenReturn(productEntity);

        ResponseEntity<?> response = productServiceImpl
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Produto atualizado com sucesso.");
    }

    @Test
    public void shouldNotFindProductToUpdate() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = productServiceImpl
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Produto não encontrado.");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnUpdateProduct() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.of(productEntity));
        Mockito.when(productMapper.toEntity(eq(productUpdateDTO), eq(productEntity), eq(Category.FOOD))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = productServiceImpl
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para atualização do produto: null");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseOnUpdateProduct() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.of(productEntity));
        Mockito.when(productMapper.toEntity(eq(productUpdateDTO), eq(productEntity), eq(Category.FOOD))).thenReturn(productEntity);
        Mockito.when(productRepository.save(eq(productEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = productServiceImpl
                .updateProduct(productDTO.getTitle(), productDTO.getSellerName(), productUpdateDTO, Category.FOOD);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao atualizar produto na base de dados: ");
    }

    @Test
    public void shouldDeleteProductWithSuccess() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.of(productEntity));

        ResponseEntity<?> response = productServiceImpl.deleteProduct(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Produto deletado com sucesso.");
    }

    @Test
    public void shouldNotFindProductForDelete() {
        Mockito.when(productRepository.findByTitleAndSellerName(eq(productDTO.getTitle()), eq(productDTO.getSellerName())))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = productServiceImpl.deleteProduct(productDTO.getTitle(), productDTO.getSellerName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Produto não encontrado.");
    }
}
