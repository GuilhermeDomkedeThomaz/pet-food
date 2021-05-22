package br.com.fatec.petfood.repository.mongo;

import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    Optional<ProductEntity> findByTitleAndSellerName(String title, String sellerName);

    Optional<List<ProductEntity>> findBySellerName(String sellerName);

    Optional<List<ProductEntity>> findAllBySellerName(String sellerName, Pageable page);

    @Query("{'title':{$regex:?0, $options:'i'}}")
    Optional<List<ProductEntity>> findAllByTitleRegex(String title);

    @Query("{'sellerName':?0, 'title':{$regex:?1, $options:'i'}}")
    Optional<List<ProductEntity>> findAllBySellerNameAndTitleRegex(String sellerName, String title, Pageable page);
}
