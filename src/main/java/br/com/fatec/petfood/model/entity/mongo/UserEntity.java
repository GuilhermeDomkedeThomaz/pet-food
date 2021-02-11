package br.com.fatec.petfood.model.entity.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

@Document(collection = "user")
public class UserEntity implements Serializable {

    @Id
    @JsonIgnore
    private ObjectId id;
    private String name;
    private String email;
    private byte[] password;
    @CreatedDate
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private DateTime defaultDateTime;

    public UserEntity() {}

    public UserEntity(String name, String email, byte[] password) {
        this.setName(name);
        this.setEmail(email);
        this.setPassword(password);
        this.setDefaultDateTime(DateTime.now());
    }

    public ObjectId getId() {
        return this.id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public DateTime getDefaultDateTime() {
        return this.defaultDateTime;
    }

    public void setDefaultDateTime(DateTime defaultDateTime) {
        this.defaultDateTime = defaultDateTime;
    }
}
