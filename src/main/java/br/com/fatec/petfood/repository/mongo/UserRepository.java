package br.com.fatec.petfood.repository.mongo;

import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByName(String name);

    Optional<UserEntity> findByEmail(String email);
}
