package br.com.fatec.petfood.repository.mongo;

import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends MongoRepository<RequestEntity, String> {
}
