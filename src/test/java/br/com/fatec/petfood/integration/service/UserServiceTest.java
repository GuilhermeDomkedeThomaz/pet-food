package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import br.com.fatec.petfood.service.UserService;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserServiceTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    private final UserDTO userDTO = EnhancedRandom.random(UserDTO.class);

    private final UserDTO userDTOWithoutName = EnhancedRandom.random(UserDTO.class, "name");

    @Test
    public void shouldCreateUserWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, Pets.DOG, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUser() {
        ResponseEntity<?> response = userService.createUser(userDTOWithoutName, Pets.DOG, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUserAlreadyExists() {
        ResponseEntity<?> response = userService.createUser(userDTO, Pets.DOG, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> secondResponse = userService.createUser(userDTO, Pets.DOG, CityZone.EAST);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Usuário já existe com o nome passado.");
    }

    @Test
    public void shouldFindUserWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, Pets.DOG, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> findResponse = userService.getUser(userDTO.getName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldNotFindUser() {
        ResponseEntity<?> findResponse = userService.getUser("Teste");

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Usuário não encontrado.");
    }

    @Test
    public void shouldLoginWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, Pets.DOG, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> loginResponse = userService.login(userDTO.getEmail(), userDTO.getPassword());

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(loginResponse.getBody(), "Login de usuário realizado com sucesso.");
    }

    @Test
    public void shouldntLogin() {
        ResponseEntity<?> response = userService.createUser(userDTO, Pets.DOG, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> loginResponse = userService.login(userDTO.getEmail(), "Teste");

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(loginResponse.getBody(), "Login de usuário inválido.");
    }
}
