package org.air_controller.secrets;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

class SecretsEncryption {

    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final int ITERATION_COUNT = 65536;

    private final String masterPassword;

    public SecretsEncryption(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public String encrypt(String secret) {
        final byte[] salt = getRandomSalt();
        final SecretKey secretKey = getSecretKey(salt);
        try {
            final Cipher cipher = Cipher.getInstance(SECRET_KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final byte[] encryptedSecretBytes = cipher.doFinal(secret.getBytes());
            return createCombinedEncryption(salt, encryptedSecretBytes);
        } catch (InvalidKeyException e) {
            System.err.println("InvalidKeyException: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("IllegalBlockSizeException: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.err.println("BadPaddingException: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.err.println("NoSuchPaddingException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException: " + e.getMessage());
        }
        return null;
    }

    public String decrypt(String encryptedSecret) {
        final byte[] combined = Base64.getDecoder().decode(encryptedSecret);
        final byte[] salt = getSaltFromCombinedEncryption(combined);
        final byte[] encryptedSecretBytes = getEncryptionFromCombinedEncryption(combined);
        final SecretKey secretKey = getSecretKey(salt);
        try {
            final Cipher cipher = Cipher.getInstance(SECRET_KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final byte[] decryptedSecretBytes = cipher.doFinal(encryptedSecretBytes);
            return new String(decryptedSecretBytes);
        } catch (InvalidKeyException e) {
            System.err.println("InvalidKeyException: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("IllegalBlockSizeException: " + e.getMessage());
        } catch (BadPaddingException e) {
            System.err.println("BadPaddingException: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.err.println("NoSuchPaddingException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException: " + e.getMessage());
        }
        return null;
    }

    private byte[] getRandomSalt() {
        final SecureRandom random = new SecureRandom();
        final byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private String createCombinedEncryption(byte[] salt, byte[] encryptedSecretBytes) {
        final byte[] combined = new byte[salt.length + encryptedSecretBytes.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(encryptedSecretBytes, 0, combined, salt.length, encryptedSecretBytes.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    private byte[] getSaltFromCombinedEncryption(byte[] combined) {
        final byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(combined, 0, salt, 0, salt.length);
        return salt;
    }

    private byte[] getEncryptionFromCombinedEncryption(byte[] combined) {
        final byte[] encryptedSecretBytes = new byte[combined.length - SALT_LENGTH];
        System.arraycopy(combined, SALT_LENGTH, encryptedSecretBytes, 0, encryptedSecretBytes.length);
        return encryptedSecretBytes;
    }

    private SecretKey getSecretKey(byte[] salt) {
        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            final KeySpec keySpec = new PBEKeySpec(masterPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            final SecretKey tempKey;
            tempKey = factory.generateSecret(keySpec);
            return new SecretKeySpec(tempKey.getEncoded(), SECRET_KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.out.println("InvalidKeySpecException: " + e.getMessage());
        }
        return null;
    }
}
