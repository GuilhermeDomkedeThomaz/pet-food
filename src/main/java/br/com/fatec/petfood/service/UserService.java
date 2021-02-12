package br.com.fatec.petfood.service;

import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> createUser(String name, String email, String password);

    ResponseEntity<?> getUser(String name) throws Exception;

    ResponseEntity<?> login(String email, String password);
}
