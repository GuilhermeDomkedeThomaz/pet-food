package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.ProductUpdateDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.SellerUpdateDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.dto.UserUpdateDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
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

        this.genericUserValidate(userDTO.getEmail(), userDTO.getRegistrationInfos(), userDTO.getPassword(),
                userDTO.getBirthdayDate(), cityZone);
    }

    @Override
    public void validateUserUpdateDTO(UserUpdateDTO userUpdateDTO, CityZone cityZone) throws Exception {
        this.genericUserValidate(userUpdateDTO.getEmail(), userUpdateDTO.getRegistrationInfos(), userUpdateDTO.getPassword(),
                userUpdateDTO.getBirthdayDate(), cityZone);
    }

    @Override
    public void validateSellerDTO(SellerDTO sellerDTO, CityZone cityZone, List<Category> categories) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(sellerDTO.getName()))
            throw new Exception("Nome passado inválido(vazio ou nulo).");

        if (sellerRepository.findByName(sellerDTO.getName()).isPresent())
            throw new Exception("Lojista já existe com o nome passado.");

        this.genericSellerValidate(sellerDTO.getEmail(), sellerDTO.getPassword(), sellerDTO.getRegistrationInfos(), cityZone, categories);
    }

    @Override
    public void validateSellerUpdateDTO(SellerUpdateDTO sellerUpdateDTO, CityZone cityZone, List<Category> categories) throws Exception {
        this.genericSellerValidate(sellerUpdateDTO.getEmail(), sellerUpdateDTO.getPassword(), sellerUpdateDTO.getRegistrationInfos(),
                cityZone, categories);
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
            String email, RegistrationInfos registrationInfos, String password, Date birthdayDate, CityZone cityZone
    ) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(email))
            throw new Exception("Email passado inválido(vazio ou nulo).");

        if (userRepository.findByEmail(email).isPresent())
            throw new Exception("Usuário já existe com o email passado.");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getDocument()))
            throw new Exception("CPF passado inválido(vazio ou nulo).");

        if (Objects.isNull(birthdayDate))
            throw new Exception("Data de Nascimento passada inválida(vazia ou nula).");

        this.genericValidate(password, registrationInfos, cityZone);
    }

    private void genericSellerValidate(
            String email, String password, RegistrationInfos registrationInfos,  CityZone cityZone, List<Category> categories
    ) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(email))
            throw new Exception("Email passado inválido(vazio ou nulo).");

        if (sellerRepository.findByEmail(email).isPresent())
            throw new Exception("Lojista já existe com o email passado.");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getDocument()))
            throw new Exception("CNPJ passado inválido(vazio ou nulo).");

        this.genericValidate(password, registrationInfos, cityZone);

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

        if (Objects.isNull(stock))
            throw new Exception("Estoque passado inválido(vazio ou nulo).");
        else if (stock <= 0)
            throw new Exception("Estoque passado inválido(menor ou igual a 0).");

        if (!validateUtils.isNotNullAndNotEmpty(imageUrl))
            throw new Exception("Url da imagem passada inválida(vazia ou nula).");
    }
}
