package br.com.fatec.petfood.repository.mongo;

import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import org.springframework.data.domain.Pageable;
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

    Optional<List<SellerEntity>> findAllByNameInAndCityZone(List<String> sellerNames, CityZone cityZone, Pageable page);

    @Query("{'categories':?0, 'cityZone':?1}")
    Optional<List<SellerEntity>> findAllByCategoryAndCityZone(Category category, CityZone cityZone, Pageable page);
}
