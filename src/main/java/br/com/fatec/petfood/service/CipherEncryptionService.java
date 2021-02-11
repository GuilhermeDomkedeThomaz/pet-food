package br.com.fatec.petfood.service;

public interface CipherEncryptionService {

    byte[] encrypt(String password) throws Exception;

    String decrypt(byte[] password) throws Exception;
}
