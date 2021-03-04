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
import br.com.fatec.petfood.utils.ResponseHeadersUtils;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
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

    @Mock
    private ResponseHeadersUtils responseHeadersUtils;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private final UserDTO userDTO = EnhancedRandom.random(UserDTO.class);
    private final UserEntity userEntity = EnhancedRandom.random(UserEntity.class);
    private final UserReturnDTO userReturnDTO = EnhancedRandom.random(UserReturnDTO.class);
    private final UserUpdateDTO userUpdateDTO = EnhancedRandom.random(UserUpdateDTO.class);

    @Test
    public void shouldCreateUserWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userMapper.toEntity(eq(userDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenReturn(userEntity);

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Usuário cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateUser() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.doThrow(new Exception("Nome passado inválido(vazio ou nulo)."))
                .when(validationService).validateUserDTO(userDTO, userEntity.getCityZone());

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperWhenCreateUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userMapper.toEntity(eq(userDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do usuário: null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseWhenCreateUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userMapper.toEntity(eq(userDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = userServiceImpl.createUser(userDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao gravar usuário na base de dados: ");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldFindUserWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toReturnDTO(userEntity)).thenReturn(userReturnDTO);

        ResponseEntity<?> response = userServiceImpl.getUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), userReturnDTO);
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.getUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorOnFindUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toReturnDTO(userEntity)).thenThrow(new NullPointerException());

        ResponseEntity<?> response = userServiceImpl.getUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do usuário: null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldLoginWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        userEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64(userEntity.getPassword()));

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.of(userEntity));

        ResponseEntity<?> response = userServiceImpl.login(userDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Login de usuário realizado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldntFindUserOnLogin() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.login(userDTO.getEmail(), userDTO.getPassword());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldntLogin() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        userEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64("4321"));

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByEmail(eq(userDTO.getEmail()))).thenReturn(Optional.of(userEntity));

        ResponseEntity<?> response = userServiceImpl.login(userDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Login de usuário inválido.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldUpdateUserWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userEntity.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toEntity(eq(userEntity), eq(userUpdateDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenReturn(userEntity);

        ResponseEntity<?> response = userServiceImpl.updateUser(userEntity.getName(), userUpdateDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Usuário atualizado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindUserToUpdate() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.updateUser(userDTO.getName(), userUpdateDTO, CityZone.EAST);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalUserErrorWithMapperOnUpdateSeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userEntity.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toEntity(eq(userEntity), eq(userUpdateDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = userServiceImpl.updateUser(userEntity.getName(), userUpdateDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para atualização do usuário: null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseOnUpdateUser() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userEntity.getName()))).thenReturn(Optional.of(userEntity));
        Mockito.when(userMapper.toEntity(eq(userEntity), eq(userUpdateDTO), eq(passwordEncrypted), eq(userEntity.getCityZone())))
                .thenReturn(userEntity);
        Mockito.when(userRepository.save(eq(userEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = userServiceImpl.updateUser(userEntity.getName(), userUpdateDTO, userEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao atualizar usuário na base de dados: ");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldDeleteUserWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.of(userEntity));

        ResponseEntity<?> response = userServiceImpl.deleteUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Usuário deletado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindUserForDelete() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(userRepository.findByName(eq(userDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userServiceImpl.deleteUser(userDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Usuário não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }
}
