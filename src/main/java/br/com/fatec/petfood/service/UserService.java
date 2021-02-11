package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.UserDTO;

public interface UserService {

    void createUser(String name, String email, String password) throws Exception;

    UserDTO getUser(String name) throws Exception;

    /*Boolean login(String email, String password);*/
}
