package br.com.fatec.petfood.repository.mongo;

import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends MongoRepository<SellerEntity, String> {

    Optional<SellerEntity> findByName(String name);

    Optional<SellerEntity> findByEmail(String email);

    @Query("{'registrationInfos.document':?0}")
    Optional<SellerEntity> findByDocument(String document);

    Optional<List<SellerEntity>> findByNameIn(List<String> sellerNames);

    @Query("{'categories':?0}")
    Optional<List<SellerEntity>> findByCategory(Category category);
}
