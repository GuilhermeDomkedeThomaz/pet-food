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
import br.com.fatec.petfood.utils.ResponseHeadersUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final ResponseHeadersUtils responseHeadersUtils;

    @Override
    public ResponseEntity<?> createProduct(ProductDTO productDTO, Category category) {
        SellerEntity seller;
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();

        try {
            seller = validationService.validateProductDTO(productDTO, category);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {
            ProductEntity productEntity = productMapper.toEntity(productDTO, seller.getId(), seller.getName(), category);

            try {
                productRepository.save(productEntity);
                return new ResponseEntity<>("Produto cadastrado com sucesso.", responseHeaders, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao gravar produto na base de dados: " + e.getMessage(),
                        responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Erro no mapeamento para criação do produto: " + e.getMessage(),
                    responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getProductByTitleAndSellerName(String title, String sellerName) {
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            ProductEntity product = productEntity.get();

            try {
                ProductReturnDTO productReturnDTO = productMapper.toReturnDTO(product);

                return new ResponseEntity<>(productReturnDTO, responseHeaders, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para retorno do produto: " + e.getMessage(),
                        responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Produto não encontrado.", responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> updateProduct(String title, String sellerName, ProductUpdateDTO productUpdateDTO, Category category) {
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            try {
                validationService.validateProductUpdateDTO(productUpdateDTO, category);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), responseHeaders, HttpStatus.BAD_REQUEST);
            }

            try {
                ProductEntity productUpdateEntity = productMapper.toEntity(productUpdateDTO, productEntity.get(), category);

                try {
                    productRepository.save(productUpdateEntity);
                    return new ResponseEntity<>("Produto atualizado com sucesso.", responseHeaders, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao atualizar produto na base de dados: " + e.getMessage(),
                            responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para atualização do produto: " + e.getMessage(),
                        responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Produto não encontrado.", responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> deleteProduct(String title, String sellerName) {
        HttpHeaders responseHeaders = responseHeadersUtils.getDefaultResponseHeaders();
        Optional<ProductEntity> productEntity = productRepository.findByTitleAndSellerName(title, sellerName);

        if (productEntity.isPresent()) {
            ProductEntity product = productEntity.get();

            try {
                productRepository.delete(product);
                return new ResponseEntity<>("Produto deletado com sucesso.", responseHeaders, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao deletar produto na base de dados: " + e.getMessage(),
                        responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Produto não encontrado.", responseHeaders, HttpStatus.BAD_REQUEST);
    }
}
