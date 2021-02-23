package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/seller")
public class SellerResource {

    private final SellerService sellerService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSeller(
            @RequestBody SellerDTO sellerDTO,
            @RequestParam(value = "cityZone") CityZone cityZone
    ) {
        return sellerService.createSeller(sellerDTO, cityZone);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUser(@RequestParam(value = "name") String name) {
        return sellerService.getSeller(name);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password
    ) {
        return sellerService.login(email, password);
    }
}
