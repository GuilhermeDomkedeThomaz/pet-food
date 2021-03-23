package br.com.fatec.petfood.service;

import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.dto.RequestUpdateDTO;
import br.com.fatec.petfood.model.enums.Status;
import org.springframework.http.ResponseEntity;

public interface RequestService {

    ResponseEntity<?> createRequest(RequestDTO requestDTO);

    ResponseEntity<?> findRequestById(String id);

    ResponseEntity<?> findRequestBySeller(String sellerName);

    ResponseEntity<?> findRequestByUser(String userName);

    ResponseEntity<?> findRequestBySellerAndUser(String sellerName, String userName);

    ResponseEntity<?> updateRequest(String id, Status status, RequestUpdateDTO requestUpdateDTO);

    ResponseEntity<?> updateStatusRequest(String id, Status status);

    ResponseEntity<?> rateRequest(String id, Integer rate);

    ResponseEntity<?> deleteRequest(String id);
}
