package br.com.fatec.petfood.service.impl;

import br.com.fatec.petfood.service.CipherEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

@Service
public class CipherEncryptionServiceImpl implements CipherEncryptionService {

    private final PublicKey publicKey;
    private final Cipher cipher;
    private final KeyPair pair;

    @Autowired
    public CipherEncryptionServiceImpl() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        pair = keyPairGen.generateKeyPair();
        publicKey = pair.getPublic();
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }

    @Override
    public byte[] encrypt(String password) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] input = password.getBytes();
        cipher.update(input);
        byte[] cipherText = cipher.doFinal();

        /*System.out.println(new String(cipherText, StandardCharsets.UTF_8));*/
        return cipherText;
    }

    @Override
    public String decrypt(byte[] password) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
        byte[] decipheredText = cipher.doFinal(password);
        String decipheredString = new String(decipheredText);

        /*System.out.println(decipheredString);*/
        return decipheredString;
    }
}
