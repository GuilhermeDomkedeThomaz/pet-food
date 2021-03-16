package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserReturnDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import br.com.fatec.petfood.model.mapper.UserMapper;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ValidationService validationService;

    @Override
    public ResponseEntity<?> createUser(UserDTO userDTO, CityZone cityZone) {
        byte[] passwordEncrypted;

        try {
            validationService.validateUserDTO(userDTO, cityZone);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            passwordEncrypted = Base64.encodeBase64(userDTO.getPassword().getBytes());
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao gerar senha criptografada para o usuário: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.nonNull(passwordEncrypted)) {
            try {
                UserEntity user = userMapper.toEntity(userDTO, passwordEncrypted, cityZone);

                try {
                    userRepository.save(user);
                    return new ResponseEntity<>("Usuário cadastrado com sucesso.", HttpStatus.CREATED);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao gravar usuário na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para criação do usuário: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Erro ao gerar senha criptografada para o usuário.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getUser(String name) {
        return this.returnUser(userRepository.findByName(name));
    }

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        return this.returnUser(userRepository.findByEmail(email));
    }

    @Override
    public ResponseEntity<?> login(String email, String password) {
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            try {
                if (!password.equals(new String(Base64.decodeBase64(user.get().getPassword()))))
                        return new ResponseEntity<>("Senha inválida para o usuário passado.", HttpStatus.BAD_REQUEST);
                else {
                    return new ResponseEntity<>("Login de usuário realizado com sucesso.", HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Login de usuário inválido: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else
            return new ResponseEntity<>("Usuário não encontrado com o email passado.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> updateUser(String document, UserUpdateDTO userUpdateDTO, CityZone cityZone) {
        byte[] passwordEncrypted;
        RegistrationInfos registrationInfos;
        Optional<UserEntity> user = userRepository.findByDocument(document);

        if (user.isPresent()) {
            try {
                registrationInfos = validationService.validateUserUpdateDTO(user.get(), userUpdateDTO, cityZone);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            try {
                passwordEncrypted = Base64.encodeBase64(userUpdateDTO.getPassword().getBytes());
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao gerar senha criptografada para o usuário: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (Objects.nonNull(passwordEncrypted)) {
                try {
                    UserEntity userEntity = userMapper.toEntity(user.get(), userUpdateDTO, registrationInfos, passwordEncrypted, cityZone);

                    try {
                        userRepository.save(userEntity);
                        return new ResponseEntity<>("Usuário atualizado com sucesso.", HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Erro ao atualizar usuário na base de dados: " + e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro no mapeamento para atualização do usuário: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else
                return new ResponseEntity<>("Erro ao gerar senha criptografada para o usuário.", HttpStatus.INTERNAL_SERVER_ERROR);
        } else
            return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> deleteUser(String name) {
        Optional<UserEntity> user = userRepository.findByName(name);

        if (user.isPresent()) {
            UserEntity userEntity = user.get();

            try {
                userRepository.delete(userEntity);
                return new ResponseEntity<>("Usuário deletado com sucesso.", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao deletar usuário na base de dados: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> returnUser(Optional<UserEntity> user) {
        if (user.isPresent()) {
            UserEntity userEntity = user.get();

            try {
                UserReturnDTO userReturnDTO = userMapper.toReturnDTO(userEntity);

                return new ResponseEntity<>(userReturnDTO, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para retorno do usuário: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Usuário não encontrado.", HttpStatus.BAD_REQUEST);
    }
}
