package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.RequestDTO;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/request")
public class RequestResource {

    private final RequestService requestService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRequest(@RequestBody RequestDTO requestDTO) {
        return requestService.createRequest(requestDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestBySeller(@RequestParam(value = "sellerName") String sellerName) {
        return requestService.findRequestBySeller(sellerName);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestByUser(@RequestParam(value = "userName") String userName) {
        return requestService.findRequestByUser(userName);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/seller/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestBySellerAndUser(
            @RequestParam(value = "sellerName") String sellerName,
            @RequestParam(value = "userName") String userName
    ) {
        return requestService.findRequestBySellerAndUser(sellerName, userName);
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
