package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.service.impl.SellerServiceImpl;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

public class SellerServiceTest extends UnitTest {

    @Mock
    private SellerMapper sellerMapper;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private ResponseHeadersUtils responseHeadersUtils;

    @InjectMocks
    private SellerServiceImpl sellerServiceImpl;

    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final List<Category> categories = Arrays.asList(Category.FOOD, Category.OTHERS);
    private final SellerReturnDTO sellerReturnDTO = EnhancedRandom.random(SellerReturnDTO.class);
    private final SellerUpdateDTO sellerUpdateDTO = EnhancedRandom.random(SellerUpdateDTO.class);

    @Test
    public void shouldCreateSellerWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerMapper.toEntity(eq(sellerDTO), eq(passwordEncrypted), eq(sellerEntity.getCityZone()), eq(categories)))
                .thenReturn(sellerEntity);
        Mockito.when(sellerRepository.save(eq(sellerEntity))).thenReturn(sellerEntity);

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateSeller() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.doThrow(new Exception("Nome passado inválido(vazio ou nulo)."))
                .when(validationService).validateSellerDTO(sellerDTO, sellerEntity.getCityZone(), categories);

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperWhenCreateSeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerMapper.toEntity(eq(sellerDTO), eq(passwordEncrypted), eq(sellerEntity.getCityZone()), eq(categories)))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do lojista: null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseWhenCreateSeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerMapper.toEntity(eq(sellerDTO), eq(passwordEncrypted), eq(sellerEntity.getCityZone()), eq(categories)))
                .thenReturn(sellerEntity);
        Mockito.when(sellerRepository.save(eq(sellerEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao gravar lojista na base de dados: ");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldFindSellerWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(sellerMapper.toReturnDTO(sellerEntity)).thenReturn(sellerReturnDTO);

        ResponseEntity<?> response = sellerServiceImpl.getSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), sellerReturnDTO);
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindSeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = sellerServiceImpl.getSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorOnFindSeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(sellerMapper.toReturnDTO(sellerEntity)).thenThrow(new NullPointerException());

        ResponseEntity<?> response = sellerServiceImpl.getSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para retorno do lojista: null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldLoginWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        sellerEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64(sellerEntity.getPassword()));

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.of(sellerEntity));

        ResponseEntity<?> response = sellerServiceImpl.login(sellerDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Login de lojista realizado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldntFindSellerOnLogin() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = sellerServiceImpl.login(sellerDTO.getEmail(), sellerDTO.getPassword());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado com o email passado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldntLogin() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        sellerEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64("4321"));

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.of(sellerEntity));

        ResponseEntity<?> response = sellerServiceImpl.login(sellerDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Login de lojista inválido.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldUpdateSellerWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerUpdateDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerEntity.getName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(sellerMapper.toEntity(eq(sellerEntity), eq(sellerUpdateDTO), eq(passwordEncrypted),
                eq(sellerEntity.getCityZone()), eq(categories))).thenReturn(sellerEntity);
        Mockito.when(sellerRepository.save(eq(sellerEntity))).thenReturn(sellerEntity);

        ResponseEntity<?> response = sellerServiceImpl
                .updateSeller(sellerEntity.getName(), sellerUpdateDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Lojista atualizado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindSellerToUpdate() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerEntity.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = sellerServiceImpl
                .updateSeller(sellerEntity.getName(), sellerUpdateDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperOnUpdateSeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerUpdateDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerEntity.getName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(sellerMapper.toEntity(eq(sellerEntity), eq(sellerUpdateDTO), eq(passwordEncrypted),
                eq(sellerEntity.getCityZone()), eq(categories))).thenThrow(new NullPointerException());

        ResponseEntity<?> response = sellerServiceImpl
                .updateSeller(sellerEntity.getName(), sellerUpdateDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para atualização do lojista: null");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseOnUpdateSeller() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");
        byte[] passwordEncrypted = Base64.encodeBase64(sellerUpdateDTO.getPassword().getBytes());

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerEntity.getName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(sellerMapper.toEntity(eq(sellerEntity), eq(sellerUpdateDTO), eq(passwordEncrypted),
                eq(sellerEntity.getCityZone()), eq(categories))).thenReturn(sellerEntity);
        Mockito.when(sellerRepository.save(eq(sellerEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = sellerServiceImpl
                .updateSeller(sellerEntity.getName(), sellerUpdateDTO, sellerEntity.getCityZone(), categories);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao atualizar lojista na base de dados: ");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldDeleteSellerWithSuccess() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.of(sellerEntity));

        ResponseEntity<?> response = sellerServiceImpl.deleteSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Lojista deletado com sucesso.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }

    @Test
    public void shouldNotFindSellerForDelete() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccessControlAllowOrigin("*");

        Mockito.when(responseHeadersUtils.getDefaultResponseHeaders()).thenReturn(responseHeaders);
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = sellerServiceImpl.deleteSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado.");
        Assertions.assertEquals(response.getHeaders().getAccessControlAllowOrigin(), "*");
    }
}
