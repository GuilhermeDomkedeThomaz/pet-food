package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.service.SellerService;
import br.com.fatec.petfood.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerMapper sellerMapper;
    private final SellerRepository sellerRepository;
    private final ValidationService validationService;

    @Override
    public ResponseEntity<?> createSeller(SellerDTO sellerDTO, CityZone cityZone) {
        byte[] passwordEncrypted;

        try {
            validationService.validateSellerDTO(sellerDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            passwordEncrypted = Base64.encodeBase64(sellerDTO.getPassword().getBytes());
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao gerar senha criptografada para o lojista: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.nonNull(passwordEncrypted)) {
            try {
                SellerEntity seller = sellerMapper.toEntity(sellerDTO, passwordEncrypted, cityZone);

                try {
                    sellerRepository.save(seller);
                    return new ResponseEntity<>("Lojista cadastrado com sucesso.", HttpStatus.CREATED);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao gravar lojista na base de dados: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para criação do lojista: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Erro ao gerar senha criptografada para o lojista.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getSeller(String name) {
        Optional<SellerEntity> seller = sellerRepository.findByName(name);

        if (seller.isPresent()) {
            SellerEntity sellerEntity = seller.get();
            SellerReturnDTO sellerReturnDTO = sellerMapper.toReturnDTO(sellerEntity);

            return new ResponseEntity<>(sellerReturnDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Lojista não encontrado.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> login(String email, String password) {
        Optional<SellerEntity> seller = sellerRepository.findByEmail(email);

        if (seller.isPresent()) {
            try {
                if (!password.equals(new String(Base64.decodeBase64(seller.get().getPassword()))))
                    return new ResponseEntity<>("Login de lojista inválido.", HttpStatus.BAD_REQUEST);
                else
                    return new ResponseEntity<>("Login de lojista realizado com sucesso.", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Login de lojista inválido: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Lojista não encontrado.", HttpStatus.BAD_REQUEST);
        }
    }
}