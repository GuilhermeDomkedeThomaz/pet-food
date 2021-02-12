package br.com.fatec.petfood.model.entity.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

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
    @CreatedDate
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private DateTime defaultDateTime;
}
