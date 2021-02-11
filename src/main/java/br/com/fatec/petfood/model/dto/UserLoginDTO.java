package br.com.fatec.petfood.model.dto;

public class UserLoginDTO {

    private String email;
    private String password;

    public UserLoginDTO() {}

    public UserLoginDTO(String email, String senha) {
        this.setEmail(email);
        this.setPassword(getPassword());
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
