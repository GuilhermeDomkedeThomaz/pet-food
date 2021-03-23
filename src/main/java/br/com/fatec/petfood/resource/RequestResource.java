package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.dto.RequestUpdateDTO;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRequest(@RequestBody RequestDTO requestDTO) {
        return requestService.createRequest(requestDTO);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestById(@RequestParam(value = "id") String id) {
        return requestService.findRequestById(id);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestBySeller(@RequestParam(value = "sellerName") String sellerName) {
        return requestService.findRequestBySeller(sellerName);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestByUser(@RequestParam(value = "userName") String userName) {
        return requestService.findRequestByUser(userName);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find/seller/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findRequestBySellerAndUser(
            @RequestParam(value = "sellerName") String sellerName,
            @RequestParam(value = "userName") String userName
    ) {
        return requestService.findRequestBySellerAndUser(sellerName, userName);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRequest(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "status") Status status,
            @RequestBody RequestUpdateDTO requestUpdateDTO
    ) {
        return requestService.updateRequest(id, status, requestUpdateDTO);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/update/status")
    public ResponseEntity<?> updateStatusRequest(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "status") Status status
    ) {
        return requestService.updateStatusRequest(id, status);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/rate")
    public ResponseEntity<?> rateRequest(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "rate") Integer rate
    ) {
        return requestService.rateRequest(id, rate);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteRequest(@RequestParam(value = "id") String id) {
        return requestService.deleteRequest(id);
    }
}
