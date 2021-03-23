package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.service.ProductService;
import br.com.fatec.petfood.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ValidationService validationService;

    @Override
    public ResponseEntity<?> createProduct(ProductDTO productDTO, Category category) {
        SellerEntity seller;

        try {
            seller = validationService.validateProductDTO(productDTO, category);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            ProductEntity productEntity = productMapper.toEntity(productDTO, seller.getId(), seller.getName(), category);

            try {
                productRepository.save(productEntity);
                return new ResponseEntity<>("Produto cadastrado com sucesso.", HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao gravar produto na base de dados: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Erro no mapeamento para criação do produto: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getProductByTitleAndSellerName(String title, String sellerName) {
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            ProductEntity product = productEntity.get();

            try {
                ProductReturnDTO productReturnDTO = productMapper.toReturnDTO(product);

                return new ResponseEntity<>(productReturnDTO, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para retorno do produto: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Produto não encontrado.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> updateProduct(String title, String sellerName, ProductUpdateDTO productUpdateDTO, Category category) {
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            try {
                validationService.validateProductUpdateDTO(productUpdateDTO, category);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            try {
                ProductEntity productUpdateEntity = productMapper.toEntity(productUpdateDTO, productEntity.get(), category);

                try {
                    productRepository.save(productUpdateEntity);
                    return new ResponseEntity<>("Produto atualizado com sucesso.", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao atualizar produto na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para atualização do produto: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Produto não encontrado.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> updateStockProduct(String title, String sellerName, Integer stock) {
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            try {
                validationService.validateProductStockUpdate(stock);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            try {
                ProductEntity productUpdateEntity = productMapper.toEntity(productEntity.get(), stock);

                try {
                    productRepository.save(productUpdateEntity);
                    return new ResponseEntity<>("Estoque do produto atualizado com sucesso.", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao atualizar estoque do produto na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para atualização de estoque do produto: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Produto não encontrado.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public void updateStockProductFromRequest(String title, String sellerName, Integer stock) {
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            ProductEntity productUpdateEntity = productMapper.toEntity(productEntity.get(), (productEntity.get().getStock() - stock));
            productRepository.save(productUpdateEntity);
        }
    }

    @Override
    public ResponseEntity<?> deleteProduct(String title, String sellerName) {
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            ProductEntity product = productEntity.get();

            try {
                productRepository.delete(product);
                return new ResponseEntity<>("Produto deletado com sucesso.", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao deletar produto na base de dados: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Produto não encontrado.", HttpStatus.BAD_REQUEST);
    }
}
