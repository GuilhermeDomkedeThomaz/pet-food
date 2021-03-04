package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.service.SellerService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

public class SellerServiceTest extends IntegrationTest {

    @Autowired
    private SellerService sellerService;

    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final List<Category> categories = Arrays.asList(Category.FOOD, Category.OTHERS);
    private final SellerUpdateDTO sellerUpdateDTO = EnhancedRandom.random(SellerUpdateDTO.class);
    private final SellerDTO sellerDTOWithoutName = EnhancedRandom.random(SellerDTO.class, "name");

    @Test
    public void shouldCreateSellerWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateSeller() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTOWithoutName, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateSellerAlreadyExists() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> secondResponse = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Lojista já existe com o nome passado.");
        Assertions.assertEquals(secondResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldFindSellerWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> findResponse = sellerService.getSeller(sellerDTO.getName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(findResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindSeller() {
        ResponseEntity<?> findResponse = sellerService.getSeller(sellerDTO.getName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Lojista não encontrado.");
        Assertions.assertEquals(findResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldLoginWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> loginResponse = sellerService.login(sellerDTO.getEmail(), sellerDTO.getPassword());

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(loginResponse.getBody(), "Login de lojista realizado com sucesso.");
        Assertions.assertEquals(loginResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldntLogin() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> loginResponse = sellerService.login(sellerDTO.getEmail(), "Teste");

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(loginResponse.getBody(), "Login de lojista inválido.");
        Assertions.assertEquals(loginResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldUpdateSellerWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> updateResponse = sellerService.updateSeller(sellerDTO.getName(), sellerUpdateDTO, CityZone.EAST, categories);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(updateResponse.getBody(), "Lojista atualizado com sucesso.");
        Assertions.assertEquals(updateResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindSellerToUpdate() {
        ResponseEntity<?> updateResponse = sellerService.updateSeller(sellerDTO.getName(), sellerUpdateDTO, CityZone.EAST, categories);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Lojista não encontrado.");
        Assertions.assertEquals(updateResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWithCategoriesOnUpdateSeller() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> updateResponse = sellerService.updateSeller(sellerDTO.getName(), sellerUpdateDTO, CityZone.EAST, null);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Categoria passada inválida(vazia ou nula).");
        Assertions.assertEquals(updateResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldDeleteSellerWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST, categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");

        ResponseEntity<?> findResponse = sellerService.deleteSeller(sellerDTO.getName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(findResponse.getBody(), "Lojista deletado com sucesso.");
        Assertions.assertEquals(findResponse.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindSellerForDelete() {
        ResponseEntity<?> response = sellerService.deleteSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }
}
