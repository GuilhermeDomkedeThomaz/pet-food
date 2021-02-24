package br.com.fatec.petfood.repository.mongo;

import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    Optional<ProductEntity> findByTitle(String title);

    Optional<ProductEntity> findByTitleAndSellerName(String title, String sellerName);
}
