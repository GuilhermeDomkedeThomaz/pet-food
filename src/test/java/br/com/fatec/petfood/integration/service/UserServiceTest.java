package br.com.fatec.petfood.integration.service;

import br.com.fatec.petfood.integration.IntegrationTest;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.enums.CityZone;
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
    private final UserUpdateDTO userUpdateDTO = EnhancedRandom.random(UserUpdateDTO.class);
    private final UserDTO userDTOWithoutName = EnhancedRandom.random(UserDTO.class, "name");

    @Test
    public void shouldCreateUserWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUser() {
        ResponseEntity<?> response = userService.createUser(userDTOWithoutName, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUserAlreadyExistsName() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> secondResponse = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Usuário já existe com o nome passado.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUserAlreadyExistsEmail() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        userDTO.setName("Teste");

        ResponseEntity<?> secondResponse = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Usuário já existe com o email passado.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUserAlreadyExistsDocument() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        userDTO.setName("Teste");
        userDTO.setEmail("Teste");

        ResponseEntity<?> secondResponse = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(secondResponse.getBody(), "Usuário já existe com o CPF passado.");
    }

    @Test
    public void shouldFindUserWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> findResponse = userService.getUser(userDTO.getName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void shouldNotFindUser() {
        ResponseEntity<?> findResponse = userService.getUser(userDTO.getName());

        Assertions.assertEquals(findResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(findResponse.getBody(), "Usuário não encontrado.");
    }

    @Test
    public void shouldLoginWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> loginResponse = userService.login(userDTO.getEmail(), userDTO.getPassword());

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(loginResponse.getBody(), "Login de usuário realizado com sucesso.");
    }

    @Test
    public void shouldntLogin() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> loginResponse = userService.login(userDTO.getEmail(), "Teste");

        Assertions.assertEquals(loginResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(loginResponse.getBody(), "Senha inválida para o usuário passado.");
    }

    @Test
    public void shouldUpdateUserWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> updateResponse = userService.updateUser(userDTO.getRegistrationInfos().getDocument(),
                userUpdateDTO, CityZone.EAST);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(updateResponse.getBody(), "Usuário atualizado com sucesso.");
    }

    @Test
    public void shouldNotFindUserToUpdate() {
        ResponseEntity<?> updateResponse = userService.updateUser(userDTO.getRegistrationInfos().getDocument(),
                userUpdateDTO, CityZone.EAST);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Usuário não encontrado.");
    }

    @Test
    public void shouldResponseBadRequestWhenUpdateUserAlreadyExistsName() {
        UserDTO secondUserDTO = EnhancedRandom.random(UserDTO.class);

        ResponseEntity<?> firstResponse = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(firstResponse.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> secondResponse = userService.createUser(secondUserDTO, CityZone.NORTH);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(secondResponse.getBody(), "Usuário cadastrado com sucesso.");

        userUpdateDTO.setName(secondUserDTO.getName());

        ResponseEntity<?> response = userService.updateUser(userDTO.getRegistrationInfos().getDocument(),
                userUpdateDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário já existe com o novo nome passado.");
    }

    @Test
    public void shouldResponseBadRequestWhenUpdateUserAlreadyExistsEmail() {
        UserDTO secondUserDTO = EnhancedRandom.random(UserDTO.class);

        ResponseEntity<?> firstResponse = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(firstResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(firstResponse.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> secondResponse = userService.createUser(secondUserDTO, CityZone.NORTH);

        Assertions.assertEquals(secondResponse.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(secondResponse.getBody(), "Usuário cadastrado com sucesso.");

        userUpdateDTO.setEmail(secondUserDTO.getEmail());

        ResponseEntity<?> response = userService.updateUser(userDTO.getRegistrationInfos().getDocument(),
                userUpdateDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário já existe com o novo email passado.");
    }

    @Test
    public void shouldResponseBadRequestWithCityZoneOnUpdateUser() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> updateResponse = userService.updateUser(userDTO.getRegistrationInfos().getDocument(),
                userUpdateDTO, null);

        Assertions.assertEquals(updateResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(updateResponse.getBody(), "Zona da cidade passada inválida(vazia ou nula).");
    }

    @Test
    public void shouldDeleteUserWithSuccess() {
        ResponseEntity<?> response = userService.createUser(userDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");

        ResponseEntity<?> deleteResponse = userService.deleteUser(userDTO.getName());

        Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(deleteResponse.getBody(), "Usuário deletado com sucesso.");
    }

    @Test
    public void shouldNotFindUserForDelete() {
        ResponseEntity<?> response = userService.deleteUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
    }
}
