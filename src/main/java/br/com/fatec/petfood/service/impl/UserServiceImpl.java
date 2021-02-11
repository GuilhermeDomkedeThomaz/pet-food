package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.CipherEncryptionService;
import br.com.fatec.petfood.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CipherEncryptionService cipherEncryptionService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CipherEncryptionService cipherEncryptionService) {
        this.userRepository = userRepository;
        this.cipherEncryptionService = cipherEncryptionService;
    }

    @Override
    public void createUser(String name, String email, String password) throws Exception {
        byte[] passwordEncrypted;

        try {
            passwordEncrypted = cipherEncryptionService.encrypt(password);
        } catch (Exception e) {
            throw new Exception("Erro ao gerar senha criptografada: " + e);
        }

        if (Objects.nonNull(passwordEncrypted)) {
            UserEntity user = new UserEntity(name, email, passwordEncrypted);

            try {
                userRepository.save(user);
            } catch (Exception e) {
                throw new Exception("Erro ao gravar usuário na base de dados: " + e);
            }
        }
    }

    @Override
    public UserDTO getUser(String name) throws Exception {
        Optional<UserEntity> user = userRepository.findByName(name);
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            UserDTO userDTO = new UserDTO();

            userDTO.setName(userEntity.getName());
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setPassword(cipherEncryptionService.decrypt(userEntity.getPassword()));

            return userDTO;
        } else {
            throw new Exception("Usuário não encontrado.");
        }
    }

   /* @Override
    public Boolean login(String email, String password) {}*/
}
