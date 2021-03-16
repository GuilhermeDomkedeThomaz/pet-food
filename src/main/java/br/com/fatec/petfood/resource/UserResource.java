package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.enums.CityZone;
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

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(
            @RequestBody UserDTO userDTO,
            @RequestParam(value = "cityZone") CityZone cityZone
    ) {
        return userService.createUser(userDTO, cityZone);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUser(@RequestParam(value = "name") String name) {
        return userService.getUser(name);
    }

    @ResponseBody
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password
    ) {
        return userService.login(email, password);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(
            @RequestParam(value = "document") String document,
            @RequestBody UserUpdateDTO userUpdateDTO,
            @RequestParam(value = "cityZone") CityZone cityZone
    ) {
        return userService.updateUser(document, userUpdateDTO, cityZone);
    }

    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "name") String name) {
        return userService.deleteUser(name);
    }
}
