package org.airController.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SecretsEncryption {

    private final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

    public SecretsEncryption(String masterPassword) {
        encryptor.setPassword(masterPassword);
    }

    public String encrypt(String secret) {
        return encryptor.encrypt(secret);
    }

    public String decrypt(String encryptedSecret) throws EncryptionOperationNotPossibleException {
        return encryptor.decrypt(encryptedSecret);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Add secret that should be encrypted:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String secret = reader.readLine();
        System.out.println("Enter the master password:");
        final String masterPassword = reader.readLine();
        final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
        final String encryptedSecret = secretsEncryption.encrypt(secret);
        System.out.println("The encrpyted secret is: " + encryptedSecret);
    }
}

