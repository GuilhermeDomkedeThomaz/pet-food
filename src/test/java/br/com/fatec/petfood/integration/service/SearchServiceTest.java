package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.service.SearchService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.List;

public class SearchServiceTest extends IntegrationTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    private String localTime = "14:00";
    private SellerReturnDTO firstSellerReturnDTO;
    private SellerReturnDTO secondSellerReturnDTO;
    private ProductReturnDTO firstProductReturnDTO;
    private final SellerEntity firstSellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final SellerEntity secondSellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final ProductEntity firstProductEntity = EnhancedRandom.random(ProductEntity.class);
    private final ProductEntity secondProductEntity = EnhancedRandom.random(ProductEntity.class);

    @BeforeEach
    public void setup() {
        firstSellerEntity.setWeekInitialTimeOperation(LocalTime.parse("10:00"));
        firstSellerEntity.setWeekFinalTimeOperation(LocalTime.parse("16:00"));
        secondSellerEntity.setWeekInitialTimeOperation(LocalTime.parse("10:00"));
        secondSellerEntity.setWeekFinalTimeOperation(LocalTime.parse("16:00"));
    }

    @Test
    public void shouldSearchSellerWithSuccess() {
        firstProductEntity.setSellerName(firstSellerEntity.getName());
        firstProductEntity.setTitle("Ração1");
        secondProductEntity.setSellerName(secondSellerEntity.getName());
        secondProductEntity.setTitle("Ração2");
        sellerRepository.save(firstSellerEntity);
        sellerRepository.save(secondSellerEntity);
        productRepository.save(firstProductEntity);
        productRepository.save(secondProductEntity);
        firstSellerReturnDTO = sellerMapper.toReturnDTO(firstSellerEntity);
        secondSellerReturnDTO = sellerMapper.toReturnDTO(secondSellerEntity);
        List<SellerReturnDTO> sellerReturnDTOList = List.of(firstSellerReturnDTO, secondSellerReturnDTO);

        ResponseEntity<?> firstResponse = searchService.searchSeller("raç", Boolean.TRUE, localTime);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(firstResponse.getBody(), sellerReturnDTOList);

        sellerRepository.deleteAll();
        productRepository.deleteAll();
        localTime = "22:00";
        firstSellerEntity.setWeekInitialTimeOperation(LocalTime.parse("20:00"));
        firstSellerEntity.setWeekFinalTimeOperation(LocalTime.parse("06:00"));
        secondSellerEntity.setWeekInitialTimeOperation(LocalTime.parse("20:00"));
        secondSellerEntity.setWeekFinalTimeOperation(LocalTime.parse("06:00"));
        firstProductEntity.setSellerName(firstSellerEntity.getName());
        firstProductEntity.setTitle("Ração1");
        secondProductEntity.setSellerName(secondSellerEntity.getName());
        secondProductEntity.setTitle("Ração2");
        sellerRepository.save(firstSellerEntity);
        sellerRepository.save(secondSellerEntity);
        productRepository.save(firstProductEntity);
        productRepository.save(secondProductEntity);
        firstSellerReturnDTO = sellerMapper.toReturnDTO(firstSellerEntity);
        secondSellerReturnDTO = sellerMapper.toReturnDTO(secondSellerEntity);
        sellerReturnDTOList = List.of(firstSellerReturnDTO, secondSellerReturnDTO);

        ResponseEntity<?> secondResponse = searchService.searchSeller("raç", Boolean.TRUE, localTime);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(secondResponse.getBody(), sellerReturnDTOList);
    }

    @Test
    public void shouldResponseBadRequestOnSearchSellerWithInvalidParams() {
        ResponseEntity<?> firstResponse = searchService.searchSeller("", Boolean.TRUE, localTime);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(firstResponse.getBody(), "Nome do produto passado inválido(vazio ou nulo).");

        ResponseEntity<?> secondResponse = searchService.searchSeller("raç", Boolean.TRUE, "AAAAA");

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
    }

    @Test
    public void shouldNotFindInSearchSeller() {
        sellerRepository.deleteAll();
        productRepository.deleteAll();
        ResponseEntity<?> response = searchService.searchSeller("raç", Boolean.TRUE, localTime);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum lojista encontrado que tenha essa produto no catálogo.");
    }

    @Test
    public void shouldSearchSellerProductsWithProductTitleWithSuccess() {
        firstProductEntity.setTitle("Ração1");
        firstProductEntity.setSellerName(firstSellerEntity.getName());
        secondProductEntity.setSellerName(firstSellerEntity.getName());
        sellerRepository.save(firstSellerEntity);
        productRepository.save(firstProductEntity);
        productRepository.save(secondProductEntity);
        firstProductReturnDTO = productMapper.toReturnDTO(firstProductEntity);
        List<ProductReturnDTO> productReturnDTOList = List.of(firstProductReturnDTO);

        ResponseEntity<?> response = searchService.searchSellerProducts(firstSellerEntity.getName(), "raç");

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), productReturnDTOList);
    }

    @Test
    public void shouldNotFindSearchSellerProductsWithProductTitle() {
        firstProductEntity.setTitle("Ração1");
        secondProductEntity.setTitle("Ração2");
        firstProductEntity.setSellerName(firstSellerEntity.getName());
        secondProductEntity.setSellerName(firstSellerEntity.getName());
        sellerRepository.save(firstSellerEntity);
        productRepository.save(firstProductEntity);
        productRepository.save(secondProductEntity);
        firstProductReturnDTO = productMapper.toReturnDTO(firstProductEntity);

        ResponseEntity<?> response = searchService.searchSellerProducts(firstSellerEntity.getName(), "AAAAA");

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum produto encontrado com título passado, cadastrado para o lojista passado.");
    }

    @Test
    public void shouldSearchSellerProductsWithoutProductTitleWithSuccess() {
        firstProductEntity.setSellerName(firstSellerEntity.getName());
        secondProductEntity.setSellerName(firstSellerEntity.getName());
        sellerRepository.save(firstSellerEntity);
        productRepository.save(firstProductEntity);
        productRepository.save(secondProductEntity);
        firstProductReturnDTO = productMapper.toReturnDTO(firstProductEntity);
        ProductReturnDTO secondProductReturnDTO = productMapper.toReturnDTO(secondProductEntity);
        List<ProductReturnDTO> productReturnDTOList = List.of(firstProductReturnDTO, secondProductReturnDTO);

        ResponseEntity<?> response = searchService.searchSellerProducts(firstSellerEntity.getName(), null);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), productReturnDTOList);
    }

    @Test
    public void shouldNotFindSearchSellerProductsWithoutProductTitle() {
        sellerRepository.deleteAll();
        ResponseEntity<?> response = searchService.searchSellerProducts(firstSellerEntity.getName(), null);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum produto cadastrado para o lojista passado.");
    }

    @Test
    public void shouldResponseBadRequestOnSearchSellerProductsWithInvalidParams() {
        ResponseEntity<?> secondResponse = searchService.searchSellerProducts(null, null);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Nome do lojista passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldSearchSellerByCategoryWithSuccess() {
        firstSellerEntity.setCategories(List.of(Category.FOOD));
        secondSellerEntity.setCategories(List.of(Category.FOOD));
        sellerRepository.save(firstSellerEntity);
        sellerRepository.save(secondSellerEntity);
        firstSellerReturnDTO = sellerMapper.toReturnDTO(firstSellerEntity);
        secondSellerReturnDTO = sellerMapper.toReturnDTO(secondSellerEntity);
        List<SellerReturnDTO> sellerReturnDTOList = List.of(firstSellerReturnDTO, secondSellerReturnDTO);

        ResponseEntity<?> firstResponse = searchService.searchSellerByCategory(Category.FOOD, Boolean.TRUE, localTime);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(firstResponse.getBody(), sellerReturnDTOList);

        sellerRepository.deleteAll();
        productRepository.deleteAll();
        localTime = "22:00";
        firstSellerEntity.setWeekInitialTimeOperation(LocalTime.parse("20:00"));
        firstSellerEntity.setWeekFinalTimeOperation(LocalTime.parse("06:00"));
        secondSellerEntity.setWeekInitialTimeOperation(LocalTime.parse("20:00"));
        secondSellerEntity.setWeekFinalTimeOperation(LocalTime.parse("06:00"));
        firstSellerEntity.setCategories(List.of(Category.FOOD));
        secondSellerEntity.setCategories(List.of(Category.FOOD));
        sellerRepository.save(firstSellerEntity);
        sellerRepository.save(secondSellerEntity);
        firstSellerReturnDTO = sellerMapper.toReturnDTO(firstSellerEntity);
        secondSellerReturnDTO = sellerMapper.toReturnDTO(secondSellerEntity);
        sellerReturnDTOList = List.of(firstSellerReturnDTO, secondSellerReturnDTO);

        ResponseEntity<?> secondResponse = searchService.searchSellerByCategory(Category.FOOD, Boolean.TRUE, localTime);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(secondResponse.getBody(), sellerReturnDTOList);
    }

    @Test
    public void shouldResponseBadRequestOnSearchSellerByCategoryWithInvalidParams() {
        ResponseEntity<?> firstResponse = searchService.searchSellerByCategory(null, Boolean.TRUE, localTime);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(firstResponse.getBody(), "Categoria passada inválida(vazia ou nula).");

        ResponseEntity<?> secondResponse = searchService.searchSellerByCategory(Category.FOOD, Boolean.TRUE, "AAAAA");

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
    }

    @Test
    public void shouldNotFindSearchSellerByCategory() {
        sellerRepository.deleteAll();
        ResponseEntity<?> response = searchService.searchSellerByCategory(Category.FOOD, Boolean.TRUE, localTime);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum lojista encontrado que tenha essa categoria cadastrada.");
    }
}
