package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.RequestDTO;
import br.com.fatec.petfood.model.dto.RequestReturnDTO;
import br.com.fatec.petfood.model.dto.RequestUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.RequestEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Status;
import br.com.fatec.petfood.model.generic.ProductRequest;
import br.com.fatec.petfood.model.mapper.RequestMapper;
import br.com.fatec.petfood.repository.mongo.RequestRepository;
import br.com.fatec.petfood.service.ProductService;
import br.com.fatec.petfood.service.RequestService;
import br.com.fatec.petfood.service.RequestValidationService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;
    private final ProductService productService;
    private final RequestRepository requestRepository;
    private final RequestValidationService requestValidationService;

    @Override
    public ResponseEntity<?> createRequest(RequestDTO requestDTO) {
        UserEntity userEntity;
        SellerEntity sellerEntity;
        List<ProductRequest> productRequests;

        try {
            requestValidationService.validateShippingPrice(requestDTO.getShippingPrice());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            sellerEntity = requestValidationService.validateSellerRequestDTO(requestDTO.getSellerName());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            userEntity = requestValidationService.validateUserRequestDTO(requestDTO.getUserName());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            productRequests = requestValidationService.validateProductsRequestDTO(requestDTO.getProducts(), requestDTO.getSellerName());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            RequestEntity requestEntity = requestMapper.toEntity(sellerEntity.getId(), sellerEntity.getName(), userEntity.getId(),
                    userEntity.getName(), productRequests, requestDTO.getShippingPrice(), Status.CREATED);

            try {
                requestValidationService.validateRequestEntityTotalValue(requestEntity);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                requestRepository.save(requestEntity);

                try {
                    this.updateStockProduct(requestEntity.getSellerName(), requestEntity.getProducts());
                } catch (Exception e) {
                    requestRepository.delete(requestEntity);
                    return new ResponseEntity<>("Erro ao atualizar estoque dos produtos do pedido na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity<>("Pedido registrado com sucesso. Id do pedido: " + requestEntity.getId().toString(),
                        HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao gravar pedido na base de dados: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Erro no mapeamento para criação do pedido: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> findRequestById(String id) {
        ObjectId objectId;

        try {
            requestValidationService.validateFindRequestById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            return new ResponseEntity<>("Id do pedido passado inválido.", HttpStatus.BAD_REQUEST);
        }

        Optional<RequestEntity> optionalRequestEntity = requestRepository.findById(objectId);

        if (optionalRequestEntity.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntity.get();

            try {
                RequestReturnDTO requestReturnDTO = requestMapper.toReturnDTO(requestEntity);
                return new ResponseEntity<>(requestReturnDTO, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para busca de pedido: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Pedido não encontrado com o id de pedido passado.",
                    HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> findRequestBySeller(String sellerName) {
        try {
            requestValidationService.validateFindRequestBySeller(sellerName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerName(sellerName);

        if (optionalRequestEntityList.isPresent()) {
            List<RequestEntity> requestEntityList = optionalRequestEntityList.get();

            if (!requestEntityList.isEmpty())
                return this.findReturn(requestEntityList);
            else
                return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de lojista passado.",
                        HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de lojista passado.",
                    HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> findRequestByUser(String userName) {
        try {
            requestValidationService.validateFindRequestByUser(userName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllByUserName(userName);

        if (optionalRequestEntityList.isPresent()) {
            List<RequestEntity> requestEntityList = optionalRequestEntityList.get();

            if (!requestEntityList.isEmpty())
                return this.findReturn(requestEntityList);
            else
                return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de usuário passado.",
                        HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Pedido(s) não encontrado(s) com o nome de usuário passado.",
                    HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> findRequestBySellerAndUser(String sellerName, String userName) {
        try {
            requestValidationService.validateFindRequestBySeller(sellerName);
            requestValidationService.validateFindRequestByUser(userName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Optional<List<RequestEntity>> optionalRequestEntityList = requestRepository.findAllBySellerNameAndUserName(sellerName, userName);

        if (optionalRequestEntityList.isPresent()) {
            List<RequestEntity> requestEntityList = optionalRequestEntityList.get();

            if (!requestEntityList.isEmpty())
                return this.findReturn(requestEntityList);
            else
                return new ResponseEntity<>("Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.",
                        HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Pedido(s) não encontrado(s) com os nomes de lojista e de usuário passados.",
                    HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> updateRequest(String id, Status status, RequestUpdateDTO requestUpdateDTO) {
        ObjectId objectId;
        Double shippingPrice;
        List<ProductRequest> productRequests;

        try {
            requestValidationService.validateFindRequestById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            return new ResponseEntity<>("Id do pedido passado inválido.", HttpStatus.BAD_REQUEST);
        }

        Optional<RequestEntity> optionalRequestEntity = requestRepository.findById(objectId);

        if (optionalRequestEntity.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntity.get();

            try {
                productRequests = requestValidationService.validateProductsRequestUpdateDTO(requestEntity, requestUpdateDTO);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }

            shippingPrice = requestValidationService.validateShippingPriceRequestUpdateDTO(requestEntity, requestUpdateDTO);

            try {
                RequestEntity requestUpdateEntity = requestMapper.toEntity(requestEntity, productRequests, shippingPrice, status);

                try {
                    requestRepository.save(requestUpdateEntity);
                    return new ResponseEntity<>("Pedido atualizado com sucesso.", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao atualizar pedido na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para atualização de pedido: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Pedido não encontrado com o id de pedido passado.",
                    HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> updateStatusRequest(String id, Status status) {
        ObjectId objectId;

        try {
            requestValidationService.validateFindRequestById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            return new ResponseEntity<>("Id do pedido passado inválido.", HttpStatus.BAD_REQUEST);
        }

        Optional<RequestEntity> optionalRequestEntity = requestRepository.findById(objectId);

        if (optionalRequestEntity.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntity.get();

            try {

                if (status.equals(Status.CANCELED)) {
                    try {
                        requestEntity.getProducts().forEach(productRequest ->
                                productRequest.setQuantity(productRequest.getQuantity() * -1));
                        this.updateStockProduct(requestEntity.getSellerName(), requestEntity.getProducts());
                    } catch (Exception e) {
                        return new ResponseEntity<>("Erro ao atualizar estoque dos produtos do pedido na base de dados: " + e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }

                RequestEntity requestUpdateEntity = requestMapper.toEntity(requestEntity, status);

                try {
                    requestRepository.save(requestUpdateEntity);
                    return new ResponseEntity<>("Status do pedido atualizado com sucesso.", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao atualizar status do pedido na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para atualização de status do pedido: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Pedido não encontrado com o id de pedido passado.",
                    HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> rateRequest(String id, Integer rate) {
        ObjectId objectId;

        try {
            requestValidationService.validateFindRequestById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            return new ResponseEntity<>("Id do pedido passado inválido.", HttpStatus.BAD_REQUEST);
        }

        Optional<RequestEntity> optionalRequestEntity = requestRepository.findById(objectId);

        if (optionalRequestEntity.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntity.get();

            try {
                RequestEntity requestUpdateEntity = requestMapper.toEntity(requestEntity, rate);

                try {
                    requestRepository.save(requestUpdateEntity);
                    return new ResponseEntity<>("Pedido avaliado com sucesso.", HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao avaliar pedido na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                return new ResponseEntity<>("Erro no mapeamento para avaliação do pedido: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Pedido não encontrado com o id de pedido passado.",
                    HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> deleteRequest(String id) {
        ObjectId objectId;

        try {
            requestValidationService.validateFindRequestById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            return new ResponseEntity<>("Id do pedido passado inválido.", HttpStatus.BAD_REQUEST);
        }

        Optional<RequestEntity> optionalRequestEntity = requestRepository.findById(objectId);

        if (optionalRequestEntity.isPresent()) {
            RequestEntity requestEntity = optionalRequestEntity.get();

            if (!requestEntity.getStatus().equals(Status.CANCELED)) {
                try {
                    requestEntity.getProducts().forEach(productRequest ->
                            productRequest.setQuantity(productRequest.getQuantity() * -1));
                    this.updateStockProduct(requestEntity.getSellerName(), requestEntity.getProducts());
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro ao atualizar estoque dos produtos do pedido na base de dados: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            try {
                requestRepository.delete(requestEntity);
                return new ResponseEntity<>("Pedido deletado com sucesso.", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Erro ao deletar pedido na base de dados: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>("Pedido não encontrado com o id de pedido passado.",
                    HttpStatus.BAD_REQUEST);
    }

    private void updateStockProduct(String sellerName, List<ProductRequest> products) {
        products.forEach(product ->
                this.productService.updateStockProductFromRequest(product.getTitle(), sellerName, product.getQuantity()));
    }

    private ResponseEntity<?> findReturn(List<RequestEntity> requestEntityList) {
        List<RequestReturnDTO> requestReturnDTOList = new ArrayList<>();

        try {
            requestEntityList.forEach(requestEntity -> requestReturnDTOList.add(requestMapper.toReturnDTO(requestEntity)));
            return new ResponseEntity<>(requestReturnDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro no mapeamento para busca de pedido(s): " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
