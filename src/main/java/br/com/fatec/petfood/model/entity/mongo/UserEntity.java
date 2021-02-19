package br.com.fatec.petfood.model.entity.mongo;

import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.enums.Pets;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.util.Date;

@Data
@RequiredArgsConstructor
@Document(collection = "user")
public class UserEntity implements Serializable {

    @Id
    @JsonIgnore
    private ObjectId id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private byte[] password;

    @NonNull
    private String document;

    @NonNull
    private String cellPhone;

    @NonNull
    private Date birthdayDate;

    @NonNull
    private String address;

    @NonNull
    private String cep;

    @NonNull
    private String city;

    @NonNull
    private CityZone cityZone;

    @NonNull
    private Pets pets;

    @CreatedDate
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private DateTime defaultDateTime;
}
