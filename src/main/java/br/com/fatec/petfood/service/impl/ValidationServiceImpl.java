package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.model.dto.SellerDTO;
import br.com.fatec.petfood.model.dto.UserDTO;
import br.com.fatec.petfood.model.generic.RegistrationInfos;
import br.com.fatec.petfood.repository.mongo.SellerRepository;
import br.com.fatec.petfood.repository.mongo.UserRepository;
import br.com.fatec.petfood.service.ValidationService;
import br.com.fatec.petfood.utils.ValidateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final ValidateUtils validateUtils;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

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

    private void genericValidate(String password, RegistrationInfos registrationInfos) throws Exception {
        if (!validateUtils.isNotNullAndNotEmpty(password))
            throw new Exception("Senha passada inválida(vazia ou nula).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getCellPhone()))
            throw new Exception("Celular passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getAddress()))
            throw new Exception("Endereço passado inválido(vazio ou nulo).");

        if (Objects.isNull(registrationInfos.getNumberAddress()))
            throw new Exception("Número do endereço passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getCep()))
            throw new Exception("Cep passado inválido(vazio ou nulo).");

        if (!validateUtils.isNotNullAndNotEmpty(registrationInfos.getCity()))
            throw new Exception("Cidade passada inválida(vazia ou nula).");
    }
}
