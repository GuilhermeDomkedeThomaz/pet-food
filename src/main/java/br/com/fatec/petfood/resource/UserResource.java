package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import br.com.fatec.petfood.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserResource {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(
            @RequestBody UserDTO userDTO,
            @RequestParam(value = "pets") Pets pets,
            @RequestParam(value = "cityZone") CityZone cityZone
    ) {
        return this.userService.createUser(userDTO, pets, cityZone);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUser(@RequestParam(value = "name") String name) throws Exception {
        return this.userService.getUser(name);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password
    ) {
        return this.userService.login(email, password);
    }
}
