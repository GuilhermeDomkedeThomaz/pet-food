package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.RequestDTO;
import org.springframework.http.ResponseEntity;

public interface RequestService {

    ResponseEntity<?> createRequest(RequestDTO requestDTO);
}
