package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/request")
public class RequestResource {

//    private final RequestService requestService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRequest() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestByUser() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestBySeller() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/user/seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestByUserAndSeller() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRequest() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/rate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rateRequest() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelRequest() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteRequest() {
        //TO DO
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
