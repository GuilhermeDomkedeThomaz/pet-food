package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.ProductDTO;
import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.entity.mongo.SellerEntity;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import br.com.fatec.petfood.repository.mongo.ProductRepository;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.ValidationService;
import br.com.fatec.petfood.utils.ValidateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void validateUserDTO(UserDTO userDTO) throws Exception {
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

        if (Objects.isNull(userDTO.getBirthdayDate()))
            throw new Exception("Data de Nascimento passada inválida(vazia ou nula).");

        this.genericValidate(userDTO.getPassword(), userDTO.getRegistrationInfos());
    }

    @Override
    public void validateSellerDTO(SellerDTO sellerDTO) throws Exception {
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

        this.genericValidate(sellerDTO.getPassword(), sellerDTO.getRegistrationInfos());
    }

    @Override
    public SellerEntity validateProductDTO(ProductDTO productDTO) throws Exception {
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

        if (!validateUtils.isNotNullAndNotEmpty(productDTO.getDescription()))
            throw new Exception("Descrição passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(productDTO.getBrand()))
            throw new Exception("Marca passada inválida(vazia ou nula).");

        if (!validateUtils.isNotNullAndNotEmpty(productDTO.getCategory()))
            throw new Exception("Categoria passada inválida(vazia ou nula).");

        if (Objects.isNull(productDTO.getPricePromotion()))
            throw new Exception("Preço de promoção passado inválido(vazio ou nulo).");
        else if (productDTO.getPricePromotion().equals(0.0))
            throw new Exception("Preço de promoção passado inválido(igual a 0).");

        if (Objects.isNull(productDTO.getPrice()))
            throw new Exception("Preço passado inválido(vazio ou nulo).");
        else if (productDTO.getPrice().equals(0.0))
            throw new Exception("Preço passado inválido(igual a 0).");

        if (Objects.isNull(productDTO.getStock()))
            throw new Exception("Estoque passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(productDTO.getImageUrl()))
            throw new Exception("Url da imagem passada inválida(vazia ou nula).");

        return sellerEntity;
    }

    private void genericValidate(String password, RegistrationInfos registrationInfos) throws Exception {
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
    }
}
