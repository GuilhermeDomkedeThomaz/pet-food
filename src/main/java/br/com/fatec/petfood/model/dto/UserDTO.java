package br.com.fatec.petfood.model.dto;

public class UserDTO extends UserLoginDTO {

    private String name;

    public UserDTO() {}

    public UserDTO(String name, String email, String senha) {
        this.setName(name);
        super.setEmail(email);
        super.setPassword(getPassword());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
