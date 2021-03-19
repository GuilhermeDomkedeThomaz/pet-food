package br.com.fatec.petfood.repository.mongo;

import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends MongoRepository<RequestEntity, String> {

    Optional<RequestEntity> findById(ObjectId id);

    Optional<List<RequestEntity>> findAllBySellerName(String sellerName);

    Optional<List<RequestEntity>> findAllByUserName(String userName);

    Optional<List<RequestEntity>> findAllBySellerNameAndUserName(String sellerName, String userName);
}
