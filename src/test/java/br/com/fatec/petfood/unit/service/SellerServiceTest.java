package br.com.fatec.petfood.unit.service;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.service.impl.SellerServiceImpl;
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

public class SellerServiceTest extends UnitTest {

    @Mock
    private SellerMapper sellerMapper;

    @Mock
    private ValidationServiceImpl validationService;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerServiceImpl sellerServiceImpl;

    private final SellerDTO sellerDTO = EnhancedRandom.random(SellerDTO.class);
    private final SellerEntity sellerEntity = EnhancedRandom.random(SellerEntity.class);
    private final SellerReturnDTO sellerReturnDTO = EnhancedRandom.random(SellerReturnDTO.class);

    @Test
    public void shouldCreateSellerWithSuccess() {
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());

        Mockito.when(sellerMapper.toEntity(eq(sellerDTO), eq(passwordEncrypted), eq(sellerEntity.getCityZone())))
                .thenReturn(sellerEntity);
        Mockito.when(sellerRepository.save(eq(sellerEntity))).thenReturn(sellerEntity);

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertEquals(response.getBody(), "Lojista cadastrado com sucesso.");
    }

    @Test
    public void shouldResponseBadRequestWhenCreateSeller() throws Exception {
        Mockito.doThrow(new Exception("Nome passado inválido(vazio ou nulo).")).when(validationService).validateSellerDTO(sellerDTO);

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Nome passado inválido(vazio ou nulo).");
    }

    @Test
    public void shouldResponseInternalServerErrorWithMapperWhenCreateSeller() {
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());

        Mockito.when(sellerMapper.toEntity(eq(sellerDTO), eq(passwordEncrypted), eq(sellerEntity.getCityZone())))
                .thenThrow(new NullPointerException());

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro no mapeamento para criação do lojista: null");
    }

    @Test
    public void shouldResponseInternalServerErrorWithDataBaseWhenCreateSeller() {
        byte[] passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());

        Mockito.when(sellerMapper.toEntity(eq(sellerDTO), eq(passwordEncrypted), eq(sellerEntity.getCityZone())))
                .thenReturn(sellerEntity);
        Mockito.when(sellerRepository.save(eq(sellerEntity))).thenThrow(new DataIntegrityViolationException(""));

        ResponseEntity<?> response = sellerServiceImpl.createSeller(sellerDTO, sellerEntity.getCityZone());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertEquals(response.getBody(), "Erro ao gravar lojista na base de dados: ");
    }

    @Test
    public void shouldFindSellerWithSuccess() {
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.of(sellerEntity));
        Mockito.when(sellerMapper.toReturnDTO(sellerEntity)).thenReturn(sellerReturnDTO);

        ResponseEntity<?> response = sellerServiceImpl.getSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), sellerReturnDTO);
    }

    @Test
    public void shouldNotFindSeller() {
        Mockito.when(sellerRepository.findByName(eq(sellerDTO.getName()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = sellerServiceImpl.getSeller(sellerDTO.getName());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado.");
    }

    @Test
    public void shouldLoginWithSuccess() {
        sellerEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64(sellerEntity.getPassword()));
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.of(sellerEntity));

        ResponseEntity<?> response = sellerServiceImpl.login(sellerDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), "Login de lojista realizado com sucesso.");
    }

    @Test
    public void shouldntFindSellerOnLogin() {
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.empty());

        ResponseEntity<?> response = sellerServiceImpl.login(sellerDTO.getEmail(), sellerDTO.getPassword());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Lojista não encontrado.");
    }

    @Test
    public void shouldntLogin() {
        sellerEntity.setPassword(Base64.encodeBase64("1234".getBytes()));
        String password = new String(Base64.decodeBase64("4321"));
        Mockito.when(sellerRepository.findByEmail(eq(sellerDTO.getEmail()))).thenReturn(Optional.of(sellerEntity));

        ResponseEntity<?> response = sellerServiceImpl.login(sellerDTO.getEmail(), password);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(response.getBody(), "Login de lojista inválido.");
    }
}
