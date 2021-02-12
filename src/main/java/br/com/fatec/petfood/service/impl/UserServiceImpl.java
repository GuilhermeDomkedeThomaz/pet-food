package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> createUser(String name, String email, String password) {
        byte[] passwordEncrypted;

        try {
            this.validateUser(name, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            passwordEncrypted = Base64.encodeBase64(password.getBytes());
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao gerar senha criptografada: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (Objects.nonNull(passwordEncrypted)) {
            UserEntity user = new UserEntity(name, email, passwordEncrypted);

            try {
                userRepository.save(user);
                return new ResponseEntity<>("Usuário cadastrado com sucesso.", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao gravar usuário na base de dados: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Erro ao gerar senha criptografada.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> getUser(String name) {
        Optional<UserEntity> user = userRepository.findByName(name);

        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            UserDTO userDTO = new UserDTO();

            userDTO.setName(userEntity.getName());
            userDTO.setEmail(userEntity.getEmail());

            try {
                userDTO.setPassword(new String(Base64.decodeBase64(userEntity.getPassword())));
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao descriptografar senha do usuário: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
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

    private void validateUser(String name, String email) throws Exception {
        if (userRepository.findByName(name).isPresent())
            throw new Exception("Usuário já existe com o nome passado.");

        if (userRepository.findByEmail(email).isPresent())
            throw new Exception("Usuário já existe com o email passado.");
    }
}
