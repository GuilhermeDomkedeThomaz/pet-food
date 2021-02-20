package br.com.fatec.petfood.model.entity.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Document(collection = "seller")
public class SellerEntity  implements Serializable {

    @Id
    @JsonIgnore
    private ObjectId id;
}
