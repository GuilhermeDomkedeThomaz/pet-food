package br.com.fatec.petfood.model.entity.mongo;

import br.com.fatec.petfood.model.enums.Status;
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
import java.util.List;

@Data
@RequiredArgsConstructor
@Document(collection = "schedule")
public class ScheduleEntity implements Serializable {

    @Id
    @JsonIgnore
    private ObjectId id;

    @NonNull
    private List<RequestEntity> requestEntityList;

    @NonNull
    private Status initialStatus;

    @NonNull
    private Status finalStatus;

    @CreatedDate
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private DateTime defaultDateTime;
}
