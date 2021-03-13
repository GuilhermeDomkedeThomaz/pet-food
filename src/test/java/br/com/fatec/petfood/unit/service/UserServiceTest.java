package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserReturnDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.mapper.UserMapper;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.impl.UserServiceImpl;
import br.com.fatec.petfood.service.impl.ValidationServiceImpl;
import br.com.fatec.petfood.unit.UnitTest;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.tomcat.util.codec.binary.Base64;
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

public class UserServiceTest extends UnitTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidationServiceImpl validationService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private final UserDTO userDTO = EnhancedRandom.random(UserDTO.class);
    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final UserReturnDTO userReturnDTO = EnhancedRandom.random(UserReturnDTO.class);
    private final UserUpdateDTO userUpdateDTO = EnhancedRandom.random(UserUpdateDTO.class);

    @Test
    public void shouldCreateUserWithSuccess() {
        byte[] passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());

        Mockito.when(userMapper.toEntity(eq(userDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenReturn(userEntity);

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUser() throws Exception {
        Mockito.doThrow(new Exception("Nome passado inválido(vazio ou nulo)."))
                .when(validationService).validateUserDTO(userDTO, userEntity.getCityZone());

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperWhenCreateUser() {
        byte[] passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());

        Mockito.when(userMapper.toEntity(eq(userDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do usuário: null");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseWhenCreateUser() {
        byte[] passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());

        Mockito.when(userMapper.toEntity(eq(userDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao gravar usuário na base de dados: ");
    }

    @Test
    public void shouldFindUserWithSuccess() {
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toReturnDTO(userEntity)).thenReturn(userReturnDTO);

        ResponseEntity<?> response = userServiceImpl.getUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), userReturnDTO);
    }

    @Test
    public void shouldNotFindUser() {
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.getUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
    }

    @Test
    public void shouldResponseInternalServerErrorOnFindUser() {
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toReturnDTO(userEntity)).thenThrow(new NullPointerException());

        ResponseEntity<?> response = userServiceImpl.getUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do usuário: null");
    }

    @Test
    public void shouldLoginWithSuccess() {
        userEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64(userEntity.getPassword()));

        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.of(userEntity));

        ResponseEntity<?> response = userServiceImpl.login(userDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Login de usuário realizado com sucesso.");
    }

    @Test
    public void shouldntFindUserOnLogin() {
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.login(userDTO.getEmail(), userDTO.getPassword());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
    }

    @Test
    public void shouldntLogin() {
        userEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64("4321"));

        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.of(userEntity));

        ResponseEntity<?> response = userServiceImpl.login(userDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Login de usuário inválido.");
    }

    @Test
    public void shouldUpdateUserWithSuccess() {
        byte[] passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());

        Mockito.when(userRepository.findByName(eq(userEntity.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toEntity(eq(userEntity), eq(userUpdateDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenReturn(userEntity);

        ResponseEntity<?> response = userServiceImpl.updateUser(userEntity.getName(), userUpdateDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Usuário atualizado com sucesso.");
    }

    @Test
    public void shouldNotFindUserToUpdate() {
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.updateUser(userDTO.getName(), userUpdateDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
    }

    @Test
    public void shouldResponseInternalUserErrorWithMapperOnUpdateSeller() {
        byte[] passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());

        Mockito.when(userRepository.findByName(eq(userEntity.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toEntity(eq(userEntity), eq(userUpdateDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = userServiceImpl.updateUser(userEntity.getName(), userUpdateDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para atualização do usuário: null");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseOnUpdateUser() {
        byte[] passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());

        Mockito.when(userRepository.findByName(eq(userEntity.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toEntity(eq(userEntity), eq(userUpdateDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = userServiceImpl.updateUser(userEntity.getName(), userUpdateDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao atualizar usuário na base de dados: ");
    }

    @Test
    public void shouldDeleteUserWithSuccess() {
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));

        ResponseEntity<?> response = userServiceImpl.deleteUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Usuário deletado com sucesso.");
    }

    @Test
    public void shouldNotFindUserForDelete() {
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.deleteUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
    }
}
