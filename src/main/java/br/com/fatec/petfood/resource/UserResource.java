package br.com.fatec.petfood.resource;

import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping(value = "/user")
public class UserResource {

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/create")
    public void createUser(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password
    ) throws Exception {
        this.userService.createUser(name, email, password);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO findUser(@RequestParam(value = "name") String name) throws Exception {
        return this.userService.getUser(name);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean login(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password
    ) throws Exception {
        return this.userService.login(email, password);
    }
}
