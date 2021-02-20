package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import br.com.fatec.petfood.model.mapper.UserMapper;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.UserService;
import br.com.fatec.petfood.utils.ValidateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ValidateUtils validateUtils;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> createUser(UserDTO userDTO, Pets pets, CityZone cityZone) {
        byte[] passwordEncrypted;

        try {
            this.validateUser(userDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao gerar senha criptografada: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.nonNull(passwordEncrypted)) {
            try {
                UserEntity user = userMapper.toEntity(userDTO, passwordEncrypted, pets, cityZone);

                try {
                    userRepository.save(user);
                    return new ResponseEntity<>("Usuário cadastrado com sucesso.", HttpStatus.CREATED);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao gravar usuário na base de dados: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para criação do usuário: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Erro ao gerar senha criptografada.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getUser(String name) {
        Optional<UserEntity> user = userRepository.findByName(name);

        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            UserReturnDTO userReturnDTO = userMapper.toReturnDTO(userEntity);

            return new ResponseEntity<>(userReturnDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> login(String email, String password) {
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            try {
                if (!password.equals(new String(Base64.decodeBase64(user.get().getPassword()))))
                    return new ResponseEntity<>("Login inválido.", HttpStatus.BAD_REQUEST);
                else
                    return new ResponseEntity<>("Login realizado com sucesso.", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Login inválido: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateUser(UserDTO userDTO) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getName()))
            throw new Exception("Nome passado inválido(vazio ou nulo).");

        if (userRepository.findByName(userDTO.getName()).isPresent())
            throw new Exception("Usuário já existe com o nome passado.");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getEmail()))
            throw new Exception("Email passado inválido(vazio ou nulo).");

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new Exception("Usuário já existe com o email passado.");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getPassword()))
            throw new Exception("Senha passada inválida(vazia ou nula).");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getRegistrationInfos().getDocument()))
            throw new Exception("CPF passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getRegistrationInfos().getCellPhone()))
            throw new Exception("Celular passado inválido(vazio ou nulo).");

        if (Objects.isNull(userDTO.getBirthdayDate()))
            throw new Exception("Data de Nascimento passada inválida(vazia ou nula).");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getRegistrationInfos().getAddress()))
            throw new Exception("Endereço passado inválido(vazio ou nulo).");

        if (Objects.isNull(userDTO.getRegistrationInfos().getNumberAddress()))
            throw new Exception("Número do endereço passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getRegistrationInfos().getCep()))
            throw new Exception("Cep passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getRegistrationInfos().getCity()))
            throw new Exception("Cidade passada inválida(vazia ou nula).");
    }
}
