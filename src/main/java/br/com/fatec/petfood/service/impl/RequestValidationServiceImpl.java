package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.ProductRequestDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.RequestValidationService;
import br.com.fatec.petfood.utils.ValidateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestValidationServiceImpl implements RequestValidationService {

    private final ValidateUtils validateUtils;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private String errors;

    @Override
    public void validateShippingPrice(Double shippingPrice) throws Exception {
        if (Objects.isNull(shippingPrice))
            throw new Exception("Valor de frete passado inválido(vazio ou nulo).");
        else if (shippingPrice <= 0.0)
            throw new Exception("Valor de frete passado inválido(menor ou igual a zero).");
    }

    @Override
    public SellerEntity validateSellerRequestDTO(String sellerName) throws Exception {
        this.genericValidateSeller(sellerName);

        Optional<SellerEntity> sellerEntity = sellerRepository.findByName(sellerName);

        if (sellerEntity.isPresent()) {
            return sellerEntity.get();
        } else
            throw new Exception("Lojista não encontrado com o nome passado.");
    }

    @Override
    public UserEntity validateUserRequestDTO(String userName) throws Exception {
        this.genericValidateUser(userName);

        Optional<UserEntity> userEntity = userRepository.findByName(userName);

        if (userEntity.isPresent()) {
            return userEntity.get();
        } else
            throw new Exception("Usuário não encontrado com o nome passado.");
    }

    @Override
    public List<ProductRequest> validateProductsRequestDTO(List<ProductRequestDTO> products, String sellerName) throws Exception {
        if (Objects.isNull(products))
            throw new Exception("Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).");

        if (products.isEmpty())
            throw new Exception("Lista de produtos atrelados ao pedido passada inválida(vazia ou nula).");

        List<ProductRequest> productRequests = new ArrayList<>();
        List<String> productErrors = new ArrayList<>();

        products.forEach(product -> {
            if (!Objects.isNull(product.getQuantity())) {
                if (product.getQuantity() > 0) {
                    Optional<ProductEntity> optionalProductEntity = productRepository.findByTitleAndSellerName(product.getTitle(), sellerName);

                    if (optionalProductEntity.isPresent()) {
                        ProductEntity productEntity = optionalProductEntity.get();

                        if (productEntity.getStock() == 0 || productEntity.getStock() < product.getQuantity()) {
                            productErrors.add(" [Produto com o título: {" + product.getTitle() +
                                    "} não tem estoque necessário. Estoque solicitado: {" + product.getQuantity() +
                                    "}, estoque atual: {" + productEntity.getStock() + "}] ");
                        } else {
                            ProductRequest productRequest = productMapper.toProductRequest(productEntity, product.getQuantity());
                            productRequests.add(productRequest);
                        }
                    } else
                        productErrors.add(" [Produto com o título: {" + product.getTitle() + "} não encontrado para o lojista passado.] ");
                } else
                    productErrors.add(" [Produto com o título: {" + product.getTitle() + "} passado com estoque inválido(menor ou igual a 0).] ");
            } else
                productErrors.add(" [Produto com o título: {" + product.getTitle() + "} passado com estoque inválido(vazio ou nulo).] ");
        });

        if (productRequests.isEmpty())
            if (!productErrors.isEmpty()) {
                this.errors = "";

                productErrors.forEach(error -> errors = errors.concat(error));

                throw new Exception("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido): " + errors);
            } else
                throw new Exception("Lista de produtos atrelados ao pedido passada inválida(nenhum produto válido para realização do pedido).");
        else if (!productErrors.isEmpty()) {
            this.errors = "";

            productErrors.forEach(error -> errors = errors.concat(error));

            throw new Exception("Produto(s) inválido(s): " + errors);
        } else
            return productRequests;
    }

    @Override
    public void validateRequestEntityTotalValue(RequestEntity requestEntity) throws Exception {
        if (Objects.isNull(requestEntity.getTotalValue()))
            throw new Exception("Erro no mapeamento para criação do pedido: Valor total não mapeado.");

        if (requestEntity.getTotalValue() <= 0.0)
            throw new Exception("Erro no mapeamento para criação do pedido: Valor total inválido(menor ou igual a zero).");
    }

    @Override
    public void validateFindRequestBySeller(String sellerName) throws Exception {
        this.genericValidateSeller(sellerName);
    }

    @Override
    public void validateFindRequestByUser(String userName) throws Exception {
        this.genericValidateUser(userName);
    }

    private void genericValidateSeller(String sellerName) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(sellerName))
            throw new Exception("Nome do lojista passado inválido(vazio ou nulo).");
    }

    private void genericValidateUser(String userName) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(userName))
            throw new Exception("Nome do usuário passado inválido(vazio ou nulo).");
    }
}
