package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> createUser(UserDTO userDTO, Pets pets, CityZone cityZone);

    ResponseEntity<?> getUser(String name) throws Exception;

    ResponseEntity<?> login(String email, String password);
}
