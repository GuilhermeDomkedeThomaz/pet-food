package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.ProductReturnDTO;
import br.com.fatec.petfood.model.dto.SellerReturnDTO;
import br.com.fatec.petfood.model.entity.mongo.ProductEntity;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.mapper.ProductMapper;
import br.com.fatec.petfood.model.mapper.SellerMapper;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.service.SearchService;
import br.com.fatec.petfood.service.ValidationService;
import br.com.fatec.petfood.utils.ValidateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SellerMapper sellerMapper;
    private final ProductMapper productMapper;
    private final ValidateUtils validateUtils;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final ValidationService validationService;

    @Override
    public ResponseEntity<?> searchSeller(String productTitle, Boolean isWeek, String localTime) {
        try {
            validationService.validateSearchSeller(productTitle, localTime);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        LocalTime parsedLocalTime = LocalTime.parse(localTime);
        Optional<List<ProductEntity>> optionalProductEntityList = productRepository.findByTitleRegex(productTitle);

        if (optionalProductEntityList.isPresent()) {
            List<ProductEntity> productEntityList = optionalProductEntityList.get();

            if (!productEntityList.isEmpty()) {
                List<String> sellerNames = new ArrayList<>();
                productEntityList.forEach(productEntity -> sellerNames.add(productEntity.getSellerName()));
                Optional<List<SellerEntity>> optionalSellerEntityList = sellerRepository.findByNameIn(sellerNames);

                if (optionalSellerEntityList.isPresent()) {
                    List<SellerEntity> sellerEntityList = optionalSellerEntityList.get();

                    if (!sellerEntityList.isEmpty()) {
                        List<SellerReturnDTO> sellerReturnDTOList = new ArrayList<>();

                        try {
                            if (!isWeek) {
                                sellerEntityList.forEach(sellerEntity -> {
                                    if (this.validateTimeOperation(
                                            parsedLocalTime, sellerEntity.getWeekendInitialTimeOperation(), sellerEntity.getWeekendFinalTimeOperation()))
                                        sellerReturnDTOList.add(sellerMapper.toReturnDTO(sellerEntity));
                                });
                            } else {
                                sellerEntityList.forEach(sellerEntity -> {
                                    if (this.validateTimeOperation(
                                            parsedLocalTime, sellerEntity.getWeekInitialTimeOperation(), sellerEntity.getWeekFinalTimeOperation()))
                                        sellerReturnDTOList.add(sellerMapper.toReturnDTO(sellerEntity));
                                });
                            }

                            return new ResponseEntity<>(sellerReturnDTOList, HttpStatus.OK);
                        } catch (Exception e) {
                            return new ResponseEntity<>("Erro no mapeamento para retorno do lojista: " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else
                        return new ResponseEntity<>("Nenhum lojista encontrado que tenha essa produto no catálogo.", HttpStatus.BAD_REQUEST);
                } else
                    return new ResponseEntity<>("Nenhum lojista encontrado que tenha essa produto no catálogo.", HttpStatus.BAD_REQUEST);
            } else
                return new ResponseEntity<>("Nenhum lojista encontrado que tenha essa produto no catálogo.", HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Nenhum lojista encontrado que tenha essa produto no catálogo.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> searchSellerProducts(String sellerName, String productTitle) {
        List<ProductEntity> productEntityList;
        Optional<List<ProductEntity>> optionalProductEntityList;
        List<ProductReturnDTO> productReturnDTOList = new ArrayList<>();

        try {
            validationService.validateSearchSellerProducts(sellerName);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (!validateUtils.isNotNullAndNotEmpty(productTitle)) {
            optionalProductEntityList = productRepository.findBySellerName(sellerName);

            if (optionalProductEntityList.isPresent()) {
                productEntityList = optionalProductEntityList.get();

                if (!productEntityList.isEmpty()) {
                    try {
                        productEntityList.forEach(productEntity -> productReturnDTOList.add(productMapper.toReturnDTO(productEntity)));
                        return new ResponseEntity<>(productReturnDTOList, HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Erro no mapeamento para retorno do produto: " + e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else
                    return new ResponseEntity<>("Nenhum produto cadastrado para o lojista passado.", HttpStatus.BAD_REQUEST);
            } else
                return new ResponseEntity<>("Nenhum produto cadastrado para o lojista passado.", HttpStatus.BAD_REQUEST);
        } else {
            optionalProductEntityList = productRepository.findBySellerNameAndTitleRegex(sellerName, productTitle);

            if (optionalProductEntityList.isPresent()) {
                productEntityList = optionalProductEntityList.get();

                if (!productEntityList.isEmpty()) {
                    try {
                        productEntityList.forEach(productEntity -> productReturnDTOList.add(productMapper.toReturnDTO(productEntity)));
                        return new ResponseEntity<>(productReturnDTOList, HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>("Erro no mapeamento para retorno do produto: " + e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else
                    return new ResponseEntity<>("Nenhum produto encontrado com título passado, cadastrado para o lojista passado.",
                            HttpStatus.BAD_REQUEST);
            } else
                return new ResponseEntity<>("Nenhum produto encontrado com título passado, cadastrado para o lojista passado.",
                        HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> searchSellerByCategory(Category category, Boolean isWeek, String localTime) {
        try {
            validationService.validateSearchSellerByCategory(category, localTime);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        LocalTime parsedLocalTime = LocalTime.parse(localTime);
        Optional<List<SellerEntity>> optionalSellerEntityList = sellerRepository.findByCategory(category);

        if (optionalSellerEntityList.isPresent()) {
            List<SellerEntity> sellerEntityList = optionalSellerEntityList.get();

            if (!sellerEntityList.isEmpty()) {
                List<SellerReturnDTO> sellerReturnDTOList = new ArrayList<>();

                try {
                    if (!isWeek) {
                        sellerEntityList.forEach(sellerEntity -> {
                            if (this.validateTimeOperation(
                                    parsedLocalTime, sellerEntity.getWeekendInitialTimeOperation(), sellerEntity.getWeekendFinalTimeOperation()))
                                sellerReturnDTOList.add(sellerMapper.toReturnDTO(sellerEntity));
                        });
                    } else {
                        sellerEntityList.forEach(sellerEntity -> {
                            if (this.validateTimeOperation(
                                    parsedLocalTime, sellerEntity.getWeekInitialTimeOperation(), sellerEntity.getWeekFinalTimeOperation()))
                                sellerReturnDTOList.add(sellerMapper.toReturnDTO(sellerEntity));
                        });
                    }

                    return new ResponseEntity<>(sellerReturnDTOList, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>("Erro no mapeamento para retorno do lojista: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else
                return new ResponseEntity<>("Nenhum lojista encontrado que tenha essa categoria cadastrada.", HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>("Nenhum lojista encontrado que tenha essa categoria cadastrada.", HttpStatus.BAD_REQUEST);
    }

    private Boolean validateTimeOperation(LocalTime now, LocalTime initialTimeOperation, LocalTime finalTimeOperation) {
        int result = finalTimeOperation.getHour() - initialTimeOperation.getHour();

        if (result > 0) {
            if (now.getHour() >= initialTimeOperation.getHour() && now.getHour() < finalTimeOperation.getHour())
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        } else if (result < 0) {
            if (now.getHour() >= initialTimeOperation.getHour()) {
                return Boolean.TRUE;
            } else if (now.getHour() < finalTimeOperation.getHour()) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else
            return Boolean.TRUE;
    }
}
