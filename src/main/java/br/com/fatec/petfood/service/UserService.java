package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> createUser(UserDTO userDTO, CityZone cityZone);

    ResponseEntity<?> getUser(String name);

    ResponseEntity<?> getUserByEmail(String email);

    ResponseEntity<?> login(String email, String password);

    ResponseEntity<?> updateUser(String document, UserUpdateDTO userUpdateDTO, CityZone cityZone);

    ResponseEntity<?> deleteUser(String name);
}
