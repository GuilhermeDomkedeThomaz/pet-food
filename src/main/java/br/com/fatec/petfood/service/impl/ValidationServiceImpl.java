package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.entity.mongo.UserEntity;
import br.com.fatec.petfood.model.enums.Category;
import br.com.fatec.petfood.model.enums.CityZone;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.ValidationService;
import br.com.fatec.petfood.utils.ValidateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final ValidateUtils validateUtils;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    @Override
    public void validateUserDTO(UserDTO userDTO, CityZone cityZone) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getName()))
            throw new Exception("Nome passado inválido(vazio ou nulo).");

        if (userRepository.findByName(userDTO.getName()).isPresent())
            throw new Exception("Usuário já existe com o nome passado.");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getEmail()))
            throw new Exception("Email passado inválido(vazio ou nulo).");

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new Exception("Usuário já existe com o email passado.");

        if (!validateUtils.isNotNullAndNotEmpty(userDTO.getRegistrationInfos().getDocument()))
            throw new Exception("CPF passado inválido(vazio ou nulo).");

        if (userRepository.findByDocument(userDTO.getRegistrationInfos().getDocument()).isPresent())
            throw new Exception("Usuário já existe com o CPF passado.");

        this.genericUserValidate(userDTO.getRegistrationInfos(), userDTO.getPassword(), userDTO.getBirthdayDate(), cityZone);
    }

    @Override
    public RegistrationInfos validateUserUpdateDTO(UserEntity userEntity, UserUpdateDTO userUpdateDTO, CityZone cityZone) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(userUpdateDTO.getName()))
            throw new Exception("Nome passado inválido(vazio ou nulo).");

        if (!userEntity.getName().equals(userUpdateDTO.getName())) {
            if (userRepository.findByName(userUpdateDTO.getName()).isPresent())
                throw new Exception("Usuário já existe com o novo nome passado.");
        }

        if (!validateUtils.isNotNullAndNotEmpty(userUpdateDTO.getEmail()))
            throw new Exception("Email passado inválido(vazio ou nulo).");

        if (!userEntity.getEmail().equals(userUpdateDTO.getEmail())) {
            if (userRepository.findByEmail(userUpdateDTO.getEmail()).isPresent())
                throw new Exception("Usuário já existe com o novo email passado.");
        }

        RegistrationInfos registrationInfos = new RegistrationInfos(
                userEntity.getRegistrationInfos().getDocument(),
                userUpdateDTO.getCellPhone(),
                userUpdateDTO.getAddress(),
                userUpdateDTO.getNumberAddress(),
                userUpdateDTO.getCep(),
                userUpdateDTO.getCity(),
                userUpdateDTO.getUf()
        );

        this.genericUserValidate(registrationInfos, userUpdateDTO.getPassword(), userUpdateDTO.getBirthdayDate(), cityZone);
        return registrationInfos;
    }

    @Override
    public void validateSellerDTO(SellerDTO sellerDTO, CityZone cityZone, List<Category> categories) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(sellerDTO.getName()))
            throw new Exception("Nome passado inválido(vazio ou nulo).");

        if (sellerRepository.findByName(sellerDTO.getName()).isPresent())
            throw new Exception("Lojista já existe com o nome passado.");

        if (!validateUtils.isNotNullAndNotEmpty(sellerDTO.getEmail()))
            throw new Exception("Email passado inválido(vazio ou nulo).");

        if (sellerRepository.findByEmail(sellerDTO.getEmail()).isPresent())
            throw new Exception("Lojista já existe com o email passado.");

        if (!validateUtils.isNotNullAndNotEmpty(sellerDTO.getRegistrationInfos().getDocument()))
            throw new Exception("CNPJ passado inválido(vazio ou nulo).");

        if (sellerRepository.findByDocument(sellerDTO.getRegistrationInfos().getDocument()).isPresent())
            throw new Exception("Lojista já existe com o CNPJ passado.");

        this.genericSellerValidate(sellerDTO.getPassword(), sellerDTO.getRegistrationInfos(), sellerDTO.getImageUrl(),
                sellerDTO.getWeekInitialTimeOperation(), sellerDTO.getWeekFinalTimeOperation(),
                sellerDTO.getWeekendInitialTimeOperation(), sellerDTO.getWeekendFinalTimeOperation(), cityZone, categories);
    }

    @Override
    public RegistrationInfos validateSellerUpdateDTO(SellerEntity sellerEntity, SellerUpdateDTO sellerUpdateDTO,
                                        CityZone cityZone, List<Category> categories) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(sellerUpdateDTO.getName()))
            throw new Exception("Nome passado inválido(vazio ou nulo).");

        if (!sellerEntity.getName().equals(sellerUpdateDTO.getName())) {
            if (sellerRepository.findByName(sellerUpdateDTO.getName()).isPresent())
                throw new Exception("Lojista já existe com o novo nome passado.");
        }

        if (!validateUtils.isNotNullAndNotEmpty(sellerUpdateDTO.getEmail()))
            throw new Exception("Email passado inválido(vazio ou nulo).");

        if (!sellerEntity.getEmail().equals(sellerUpdateDTO.getEmail())) {
            if (sellerRepository.findByEmail(sellerUpdateDTO.getEmail()).isPresent())
                throw new Exception("Lojista já existe com o novo email passado.");
        }

        RegistrationInfos registrationInfos = new RegistrationInfos(
                sellerEntity.getRegistrationInfos().getDocument(),
                sellerUpdateDTO.getCellPhone(),
                sellerUpdateDTO.getAddress(),
                sellerUpdateDTO.getNumberAddress(),
                sellerUpdateDTO.getCep(),
                sellerUpdateDTO.getCity(),
                sellerUpdateDTO.getUf()
        );

        this.genericSellerValidate(sellerUpdateDTO.getPassword(), registrationInfos, sellerUpdateDTO.getImageUrl(),
                sellerUpdateDTO.getWeekInitialTimeOperation(), sellerUpdateDTO.getWeekFinalTimeOperation(),
                sellerUpdateDTO.getWeekendInitialTimeOperation(), sellerUpdateDTO.getWeekendFinalTimeOperation(), cityZone, categories);
        return registrationInfos;
    }

    @Override
    public SellerEntity validateProductDTO(ProductDTO productDTO, Category category) throws Exception {
        Optional<SellerEntity> optionalSellerEntity;
        SellerEntity sellerEntity;

        if (!validateUtils.isNotNullAndNotEmpty(productDTO.getSellerName()))
            throw new Exception("Nome do lojista passado inválido(vazio ou nulo).");

        try {
            optionalSellerEntity = sellerRepository.findByName(productDTO.getSellerName());
        } catch (Exception e) {
            throw new Exception("Erro ao buscar lojista na base de dados com o nome passado: " + e.getMessage());
        }

        if (optionalSellerEntity.isPresent()) {
            sellerEntity = optionalSellerEntity.get();
        } else {
            throw new Exception("Lojista não encontrado com o nome passado.");
        }

        if (!validateUtils.isNotNullAndNotEmpty(productDTO.getTitle()))
            throw new Exception("Título passado inválido(vazio ou nulo).");

        if (productRepository.findByTitleAndSellerName(productDTO.getTitle(), sellerEntity.getName()).isPresent())
            throw new Exception("Título passado já cadastrado para o lojista passado.");

        this.genericProductValidate(
                productDTO.getDescription(), productDTO.getBrand(), category, productDTO.getPricePromotion(),
                productDTO.getPrice(), productDTO.getStock(), productDTO.getImageUrl()
        );

        return sellerEntity;
    }

    @Override
    public void validateProductUpdateDTO(ProductUpdateDTO productUpdateDTO, Category category) throws Exception {
        this.genericProductValidate(
                productUpdateDTO.getDescription(), productUpdateDTO.getBrand(), category,
                productUpdateDTO.getPricePromotion(), productUpdateDTO.getPrice(), productUpdateDTO.getStock(),
                productUpdateDTO.getImageUrl()
        );
    }

    @Override
    public void validateProductStockUpdate(Integer stock) throws Exception {
        this.genericProductStockValidate(stock);
    }

    @Override
    public void validateSearchSeller(String productTitle, String localTime) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(productTitle))
            throw new Exception("Nome do produto passado inválido(vazio ou nulo).");

        this.genericLocalTimeValidate(localTime);
    }

    @Override
    public void searchSellerProducts(String sellerName) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(sellerName))
            throw new Exception("Nome do lojista passado inválido(vazio ou nulo).");
    }

    @Override
    public void searchSellerByCategory(Category category, String localTime) throws Exception {
        if (Objects.isNull(category))
            throw new Exception("Categoria passada inválida(vazia ou nula).");

        this.genericLocalTimeValidate(localTime);
    }

    private void genericValidate(String password, RegistrationInfos registrationInfos, CityZone cityZone) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(password))
            throw new Exception("Senha passada inválida(vazia ou nula).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getCellPhone()))
            throw new Exception("Celular passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getAddress()))
            throw new Exception("Endereço passado inválido(vazio ou nulo).");

        if (Objects.isNull(registrationInfos.getNumberAddress()))
            throw new Exception("Número do endereço passado inválido(vazio ou nulo).");
        else if (registrationInfos.getNumberAddress().equals(0))
            throw new Exception("Número do endereço passado inválido(igual a 0).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getCep()))
            throw new Exception("Cep passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getCity()))
            throw new Exception("Cidade passada inválida(vazia ou nula).");

        if (Objects.isNull(cityZone))
            throw new Exception("Zona da cidade passada inválida(vazia ou nula).");
    }

    private void genericUserValidate(
            RegistrationInfos registrationInfos, String password, Date birthdayDate, CityZone cityZone
    ) throws Exception {
        if (Objects.isNull(birthdayDate))
            throw new Exception("Data de Nascimento passada inválida(vazia ou nula).");

        this.genericValidate(password, registrationInfos, cityZone);
    }

    private void genericSellerValidate(
            String password, RegistrationInfos registrationInfos, String imageUrl, String weekInitialTimeOperation,
            String weekFinalTimeOperation, String weekendInitialTimeOperation, String weekendFinalTimeOperation,
            CityZone cityZone, List<Category> categories
    ) throws Exception {
        this.genericValidate(password, registrationInfos, cityZone);

        if (!validateUtils.isNotNullAndNotEmpty(imageUrl))
            throw new Exception("Url da imagem passada inválida(vazia ou nula).");

        if (!validateUtils.isNotNullAndNotEmpty(weekInitialTimeOperation))
            throw new Exception("Horário inicial de funcionamento durante a semana passado inválido(vazio ou nulo).");
        else {
            try {
                LocalTime.parse(weekInitialTimeOperation);
            } catch (Exception e) {
                throw new Exception("Horário inicial de funcionamento durante a semana passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
            }
        }

        if (!validateUtils.isNotNullAndNotEmpty(weekFinalTimeOperation))
            throw new Exception("Horário final de funcionamento durante a semana passado inválido(vazio ou nulo).");
        else {
            try {
                LocalTime.parse(weekInitialTimeOperation);
            } catch (Exception e) {
                throw new Exception("Horário final de funcionamento durante a semana passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
            }
        }

        if (!validateUtils.isNotNullAndNotEmpty(weekendInitialTimeOperation))
            throw new Exception("Horário inicial de funcionamento durante o final de semana passado inválido(vazio ou nulo).");
        else {
            try {
                LocalTime.parse(weekInitialTimeOperation);
            } catch (Exception e) {
                throw new Exception("Horário inicial de funcionamento durante o final de semana passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
            }
        }

        if (!validateUtils.isNotNullAndNotEmpty(weekendFinalTimeOperation))
            throw new Exception("Horário final de funcionamento durante o final de semana passado inválido(vazio ou nulo).");
        else {
            try {
                LocalTime.parse(weekInitialTimeOperation);
            } catch (Exception e) {
                throw new Exception("Horário final de funcionamento durante o final de semana passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
            }
        }

        if (Objects.isNull(categories))
            throw new Exception("Categoria passada inválida(vazia ou nula).");
        else if (categories.isEmpty())
            throw new Exception("Categoria passada inválida(vazia ou nula).");
    }

    private void genericProductValidate(
            String description, String brand, Category category, Double pricePromotion, Double price, Integer stock, String imageUrl
    ) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(description))
            throw new Exception("Descrição passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(brand))
            throw new Exception("Marca passada inválida(vazia ou nula).");

        if (Objects.isNull(category))
            throw new Exception("Categoria passada inválida(vazia ou nula).");

        if (Objects.isNull(pricePromotion))
            throw new Exception("Preço de promoção passado inválido(vazio ou nulo).");
        else if (pricePromotion <= 0.0)
            throw new Exception("Preço de promoção passado inválido(menor ou igual a 0).");

        if (Objects.isNull(price))
            throw new Exception("Preço passado inválido(vazio ou nulo).");
        else if (price <= 0.0)
            throw new Exception("Preço passado inválido(menor ou igual a 0).");

        this.genericProductStockValidate(stock);

        if (!validateUtils.isNotNullAndNotEmpty(imageUrl))
            throw new Exception("Url da imagem passada inválida(vazia ou nula).");
    }

    private void genericProductStockValidate(Integer stock) throws Exception {
        if (Objects.isNull(stock))
            throw new Exception("Estoque passado inválido(vazio ou nulo).");
        else if (stock <= 0)
            throw new Exception("Estoque passado inválido(menor ou igual a 0).");
    }

    private void genericLocalTimeValidate(String localTime) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(localTime))
            throw new Exception("Horário passado inválido(vazio ou nulo).");
        else {
            try {
                LocalTime.parse(localTime);
            } catch (Exception e) {
                throw new Exception("Horário passado inválido. Favor passar no seguinte formato: 'HH:MM'.");
            }
        }
    }
}
