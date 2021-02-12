package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void createUser(String name, String email, String password) throws Exception {
        byte[] passwordEncrypted = this.validateUser(name, email, password);

        if (Objects.nonNull(passwordEncrypted)) {
            UserEntity user = new UserEntity(name, email, passwordEncrypted);

            try {
                userRepository.save(user);
            } catch (Exception e) {
                throw new Exception("Erro ao gravar usuário na base de dados: " + e);
            }
        } else
            throw new Exception("Erro ao gerar senha criptografada.");
    }

    @Override
    public UserDTO getUser(String name) throws Exception {
        Optional<UserEntity> user = userRepository.findByName(name);

        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            UserDTO userDTO = new UserDTO();

            userDTO.setName(userEntity.getName());
            userDTO.setEmail(userEntity.getEmail());

            try {
                userDTO.setPassword(new String(Base64.decodeBase64(userEntity.getPassword())));
            } catch (Exception e) {
                throw new Exception("Erro ao descriptografar senha do usuário.");
            }

            return userDTO;
        } else {
            throw new Exception("Usuário não encontrado.");
        }
    }

    @Override
    public Boolean login(String email, String password) throws Exception {
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            try {
                if (password.equals(new String(Base64.decodeBase64(user.get().getPassword()))))
                    return Boolean.TRUE;
                else
                    return Boolean.FALSE;
            } catch (Exception e) {
                return false;
            }
        } else {
            throw new Exception("Usuário não encontrado.");
        }
    }

    private byte[] validateUser(String name, String email, String password) throws Exception {
        byte[] passwordEncrypted;

        if (userRepository.findByName(name).isPresent())
            throw new Exception("Usuário já existe com o nome passado.");

        if (userRepository.findByEmail(email).isPresent())
            throw new Exception("Usuário já existe com o email passado.");

        try {
            passwordEncrypted = Base64.encodeBase64(password.getBytes());
        } catch (Exception e) {
            throw new Exception("Erro ao gerar senha criptografada: " + e);
        }

        return passwordEncrypted;
    }
}
