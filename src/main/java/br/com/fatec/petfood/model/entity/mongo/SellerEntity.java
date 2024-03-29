package br.com.fatec.petfood.model.entity.mongo;

import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
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
import java.time.LocalTime;
import java.util.List;

@Data
@RequiredArgsConstructor
@Document(collection = "seller")
public class SellerEntity  implements Serializable {

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
    private RegistrationInfos registrationInfos;

    @NonNull
    private String imageUrl;

    @NonNull
    private LocalTime weekInitialTimeOperation;

    @NonNull
    private LocalTime weekFinalTimeOperation;

    @NonNull
    private LocalTime weekendInitialTimeOperation;

    @NonNull
    private LocalTime weekendFinalTimeOperation;

    @NonNull
    private CityZone cityZone;

    @NonNull
    private List<Category> categories;

    @CreatedDate
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private DateTime defaultDateTime;
}
