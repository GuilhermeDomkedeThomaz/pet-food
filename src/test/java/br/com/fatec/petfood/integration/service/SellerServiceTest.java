package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.service.SellerService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SellerServiceTest extends IntegrationTest {

    @Autowired
    private SellerService sellerService;

    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final SellerDTO sellerDTOWithoutName = EnhancedRandom.random(SellerDTO.class, "name");

    @Test
    public void shouldCreateSellerWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateSeller() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTOWithoutName, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateSellerAlreadyExists() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> secondResponse = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Lojista já existe com o nome passado.");
    }

    @Test
    public void shouldFindSellerWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> findResponse = sellerService.getSeller(sellerDTO.getName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldNotFindSeller() {
        ResponseEntity<?> findResponse = sellerService.getSeller("Teste");

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Lojista não encontrado.");
    }

    @Test
    public void shouldLoginWithSuccess() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> loginResponse = sellerService.login(sellerDTO.getEmail(), sellerDTO.getPassword());

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(loginResponse.getBody(), "Login de lojista realizado com sucesso.");
    }

    @Test
    public void shouldntLogin() {
        ResponseEntity<?> response = sellerService.createSeller(sellerDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");

        ResponseEntity<?> loginResponse = sellerService.login(sellerDTO.getEmail(), "Teste");

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(loginResponse.getBody(), "Login de lojista inválido.");
    }
}
