package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.service.impl.SearchServiceImpl;
import br.com.fatec.petfood.service.impl.ValidationServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import br.com.fatec.petfood.utils.ValidateUtils;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

public class SearchServiceTest extends UnitTest {

    @Mock
    private SellerMapper sellerMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ValidateUtils validateUtils;

    @Mock
    private ValidationServiceImpl validationService;

    @InjectMocks
    private SearchServiceImpl searchServiceImpl;

    private String localTime = "14:00";
    private List<SellerEntity> sellerEntityList;
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final List<SellerReturnDTO> sellerReturnDTOList = List.of(EnhancedRandom.random(SellerReturnDTO.class));
    private final List<ProductEntity> productEntityList = List.of(EnhancedRandom.random(ProductEntity.class));
    private final List<ProductReturnDTO> productReturnDTOList = List.of(EnhancedRandom.random(ProductReturnDTO.class));

    @BeforeEach
    public void setup() {
        sellerEntity.setWeekInitialTimeOperation(LocalTime.parse("10:00"));
        sellerEntity.setWeekFinalTimeOperation(LocalTime.parse("16:00"));
        sellerEntity.setCityZone(CityZone.EAST);
        sellerEntityList = List.of(sellerEntity);
    }

    @Test
    public void shouldSearchSellerWithSuccess() {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        SellerReturnDTO sellerReturnDTO = sellerReturnDTOList.get(0);
        ProductEntity productEntity = productEntityList.get(0);

        Mockito.when(productRepository.findAllByTitleRegex(eq(productEntity.getTitle())))
                .thenReturn(Optional.of(productEntityList));
        Mockito.when(sellerRepository.findAllByNameInAndCityZone(eq(List.of(productEntity.getSellerName())), eq(CityZone.EAST), eq(PageRequest.of(0, 100))))
                .thenReturn(Optional.of(sellerEntityList));
        Mockito.when(sellerMapper.toReturnDTO(eq(sellerEntity))).thenReturn(sellerReturnDTO);

        ResponseEntity<?> firstResponse = searchServiceImpl.searchSeller(productEntity.getTitle(), CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(firstResponse.getBody(), sellerReturnDTOList);

        localTime = "22:00";
        sellerEntity.setWeekInitialTimeOperation(LocalTime.parse("20:00"));
        sellerEntity.setWeekFinalTimeOperation(LocalTime.parse("06:00"));
        sellerEntityList = List.of(sellerEntity);
        sellerEntity = sellerEntityList.get(0);
        sellerReturnDTO = sellerReturnDTOList.get(0);
        productEntity = productEntityList.get(0);

        Mockito.when(productRepository.findAllByTitleRegex(eq(productEntity.getTitle())))
                .thenReturn(Optional.of(productEntityList));
        Mockito.when(sellerRepository.findAllByNameInAndCityZone(eq(List.of(productEntity.getSellerName())), eq(CityZone.EAST), eq(PageRequest.of(0, 100))))
                .thenReturn(Optional.of(sellerEntityList));
        Mockito.when(sellerMapper.toReturnDTO(eq(sellerEntity))).thenReturn(sellerReturnDTO);

        ResponseEntity<?> secondResponse = searchServiceImpl.searchSeller(productEntity.getTitle(), CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(secondResponse.getBody(), sellerReturnDTOList);
    }

    @Test
    public void shouldResponseBadRequestOnSearchSellerWithInvalidParams() throws Exception {
        String localTime = "AAAA";
        ProductEntity productEntity = productEntityList.get(0);

        Mockito.doThrow(new Exception("Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'."))
                .when(validationService).validateSearchSeller(eq(productEntity.getTitle()), eq(CityZone.EAST), eq(localTime), eq(0), eq(100));

        ResponseEntity<?> response = searchServiceImpl.searchSeller(productEntity.getTitle(), CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
    }

    @Test
    public void shouldNotFindProductsInSearchSeller() {
        Mockito.when(productRepository.findAllByTitleRegex(eq(productEntityList.get(0).getTitle())))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = searchServiceImpl.searchSeller(productEntityList.get(0).getTitle(), CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum lojista encontrado que tenha essa produto no catálogo.");
    }

    @Test
    public void shouldNotFindSellersInSearchSeller() {
        Mockito.when(productRepository.findAllByTitleRegex(eq(productEntityList.get(0).getTitle())))
                .thenReturn(Optional.of(productEntityList));
        Mockito.when(sellerRepository.findAllByNameInAndCityZone(eq(List.of(productEntityList.get(0).getSellerName())), eq(CityZone.EAST), eq(PageRequest.of(0, 100))))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = searchServiceImpl.searchSeller(productEntityList.get(0).getTitle(), CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum lojista encontrado que tenha essa produto no catálogo.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnSearchSeller() {
        Mockito.when(productRepository.findAllByTitleRegex(eq(productEntityList.get(0).getTitle())))
                .thenReturn(Optional.of(productEntityList));
        Mockito.when(sellerRepository.findAllByNameInAndCityZone(eq(List.of(productEntityList.get(0).getSellerName())), eq(CityZone.EAST), eq(PageRequest.of(0, 100))))
                .thenReturn(Optional.of(sellerEntityList));
        Mockito.when(sellerMapper.toReturnDTO(eq(sellerEntityList.get(0)))).thenThrow(new NullPointerException(""));

        ResponseEntity<?> response = searchServiceImpl.searchSeller(productEntityList.get(0).getTitle(), CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do lojista: ");
    }

    @Test
    public void shouldSearchSellerProductsWithProductTitleWithSuccess() {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        ProductEntity productEntity = productEntityList.get(0);
        ProductReturnDTO productReturnDTO = productReturnDTOList.get(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(eq(productEntity.getTitle()))).thenReturn(Boolean.TRUE);
        Mockito.when(productRepository.findAllBySellerNameAndTitleRegex(eq(sellerEntity.getName()), eq(productEntity.getTitle()), eq(PageRequest.of(0, 100))))
                .thenReturn(Optional.of(productEntityList));
        Mockito.when(productMapper.toReturnDTO(eq(productEntity))).thenReturn(productReturnDTO);

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), productEntity.getTitle(), 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), productReturnDTOList);
    }

    @Test
    public void shouldResponseBadRequestOnSearchSellerProductsWithProductTitleWithInvalidParams() throws Exception {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        ProductEntity productEntity = productEntityList.get(0);

        Mockito.doThrow(new Exception("Página passada inválida(vazia ou nula)."))
                .when(validationService).validateSearchSellerProducts(eq(sellerEntity.getName()), eq(0),eq(100));

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), productEntity.getTitle(), 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Página passada inválida(vazia ou nula).");
    }

    @Test
    public void shouldNotFindSearchSellerProductsWithProductTitle() {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        ProductEntity productEntity = productEntityList.get(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(eq(productEntity.getTitle()))).thenReturn(Boolean.TRUE);
        Mockito.when(productRepository.findAllBySellerNameAndTitleRegex(eq(sellerEntity.getName()), eq(productEntity.getTitle()), eq(PageRequest.of(0, 100))))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), productEntity.getTitle(), 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum produto encontrado com título passado, cadastrado para o lojista passado.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnSearchSellerProductsWithProductTitle() {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        ProductEntity productEntity = productEntityList.get(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty(eq(productEntity.getTitle()))).thenReturn(Boolean.TRUE);
        Mockito.when(productRepository.findAllBySellerNameAndTitleRegex(eq(sellerEntity.getName()), eq(productEntity.getTitle()), eq(PageRequest.of(0, 100))))
                .thenReturn(Optional.of(productEntityList));
        Mockito.when(productMapper.toReturnDTO(eq(productEntity))).thenThrow(new NullPointerException(""));

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), productEntity.getTitle(), 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do produto: ");
    }

    @Test
    public void shouldSearchSellerProductsWithoutProductTitleWithSuccess() {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        ProductEntity productEntity = productEntityList.get(0);
        ProductReturnDTO productReturnDTO = productReturnDTOList.get(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty("")).thenReturn(Boolean.FALSE);
        Mockito.when(productRepository.findAllBySellerName(eq(sellerEntity.getName()), eq(PageRequest.of(0, 100)))).thenReturn(Optional.of(productEntityList));
        Mockito.when(productMapper.toReturnDTO(eq(productEntity))).thenReturn(productReturnDTO);

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), "", 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), productReturnDTOList);
    }

    @Test
    public void shouldResponseBadRequestOnSearchSellerProductsWithoutProductTitleWithInvalidParams() throws Exception {
        SellerEntity sellerEntity = sellerEntityList.get(0);

        Mockito.doThrow(new Exception("Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'."))
                .when(validationService).validateSearchSellerProducts(eq(sellerEntity.getName()), eq(0), eq(100));

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), "", 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
    }

    @Test
    public void shouldNotFindSearchSellerProductsWithoutProductTitle() {
        SellerEntity sellerEntity = sellerEntityList.get(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty("")).thenReturn(Boolean.FALSE);
        Mockito.when(productRepository.findAllBySellerName(eq(sellerEntity.getName()), eq(PageRequest.of(0, 100)))).thenReturn(Optional.empty());

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), "", 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum produto cadastrado para o lojista passado.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnSearchSellerProductsWithoutProductTitle() {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        ProductEntity productEntity = productEntityList.get(0);

        Mockito.when(validateUtils.isNotNullAndNotEmpty("")).thenReturn(Boolean.FALSE);
        Mockito.when(productRepository.findAllBySellerName(eq(sellerEntity.getName()), eq(PageRequest.of(0, 100)))).thenReturn(Optional.of(productEntityList));
        Mockito.when(productMapper.toReturnDTO(eq(productEntity))).thenThrow(new NullPointerException(""));

        ResponseEntity<?> response = searchServiceImpl.searchSellerProducts(sellerEntity.getName(), "", 0 ,100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do produto: ");
    }

    @Test
    public void shouldSearchSellerByCategoryWithSuccess() {
        SellerEntity sellerEntity = sellerEntityList.get(0);
        SellerReturnDTO sellerReturnDTO = sellerReturnDTOList.get(0);

        Mockito.when(sellerRepository.findAllByCategoryAndCityZone(eq(Category.FOOD), eq(CityZone.EAST), eq(PageRequest.of(0, 100)))).thenReturn(Optional.of(sellerEntityList));
        Mockito.when(sellerMapper.toReturnDTO(eq(sellerEntity))).thenReturn(sellerReturnDTO);

        ResponseEntity<?> firstResponse = searchServiceImpl.searchSellerByCategory(Category.FOOD, CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(firstResponse.getBody(), sellerReturnDTOList);

        localTime = "22:00";
        sellerEntity.setWeekInitialTimeOperation(LocalTime.parse("20:00"));
        sellerEntity.setWeekFinalTimeOperation(LocalTime.parse("06:00"));
        sellerEntityList = List.of(sellerEntity);
        sellerEntity = sellerEntityList.get(0);
        sellerReturnDTO = sellerReturnDTOList.get(0);

        Mockito.when(sellerRepository.findAllByCategoryAndCityZone(eq(Category.FOOD), eq(CityZone.EAST), eq(PageRequest.of(0, 100)))).thenReturn(Optional.of(sellerEntityList));
        Mockito.when(sellerMapper.toReturnDTO(eq(sellerEntity))).thenReturn(sellerReturnDTO);

        ResponseEntity<?> secondResponse = searchServiceImpl.searchSellerByCategory(Category.FOOD, CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(secondResponse.getBody(), sellerReturnDTOList);
    }

    @Test
    public void shouldResponseBadRequestOnSearchSellerByCategoryWithInvalidParams() throws Exception {
        String localTime = "AAAA";

        Mockito.doThrow(new Exception("Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'."))
                .when(validationService).validateSearchSellerByCategory(eq(Category.FOOD), eq(CityZone.EAST), eq(localTime), eq(0), eq(100));

        ResponseEntity<?> response = searchServiceImpl.searchSellerByCategory(Category.FOOD, CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
    }

    @Test
    public void shouldNotFindSearchSellerByCategory() {
        Mockito.when(sellerRepository.findAllByCategoryAndCityZone(eq(Category.FOOD), eq(CityZone.EAST), eq(PageRequest.of(0, 100)))).thenReturn(Optional.empty());

        ResponseEntity<?> response = searchServiceImpl.searchSellerByCategory(Category.FOOD, CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nenhum lojista encontrado que tenha essa categoria cadastrada.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnSearchSellerByCategory() {
        SellerEntity sellerEntity = sellerEntityList.get(0);

        Mockito.when(sellerRepository.findAllByCategoryAndCityZone(eq(Category.FOOD), eq(CityZone.EAST), eq(PageRequest.of(0, 100)))).thenReturn(Optional.of(sellerEntityList));
        Mockito.when(sellerMapper.toReturnDTO(eq(sellerEntity))).thenThrow(new NullPointerException(""));

        ResponseEntity<?> response = searchServiceImpl.searchSellerByCategory(Category.FOOD, CityZone.EAST, Boolean.TRUE, localTime, 0, 100);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do lojista: ");
    }
}
