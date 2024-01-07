package org.airController.secrets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecretsEncryptionTest {

    private final String MASTER_TEST_PASSWORD = "TEST";

    @ParameterizedTest(name = "{index} => secret={0}")
    @ArgumentsSource(SecretsEncryptionArgumentProvider.class)
    void testEncryptenAndDecryption(String secret) {
        final SecretsEncryption testee = new SecretsEncryption(MASTER_TEST_PASSWORD);

        final String encryptedSecret = testee.encrypt(secret);
        final String decryptedSecret = testee.decrypt(encryptedSecret);

        assertEquals(decryptedSecret, secret);
    }

    @ParameterizedTest(name = "{index} => secret={0}")
    @ArgumentsSource(SecretsEncryptionArgumentProvider.class)
    void testEncryptenAndDecryptionWithNewInstance(String secret) {
        final SecretsEncryption encryptionTestee = new SecretsEncryption(MASTER_TEST_PASSWORD);
        final String encryptedSecret = encryptionTestee.encrypt(secret);

        final SecretsEncryption decryptionTestee = new SecretsEncryption(MASTER_TEST_PASSWORD);
        final String decryptedSecret = decryptionTestee.decrypt(encryptedSecret);

        assertEquals(decryptedSecret, secret);
    }

    static class SecretsEncryptionArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("Hello world"),
                    Arguments.of("123345"),
                    Arguments.of("1"),
                    Arguments.of("Z"),
                    Arguments.of("ö;-")
            );
        }
    }

    @Test
    void testWhenWrongPasswordThenThrow() {
        final SecretsEncryption encryptionTestee = new SecretsEncryption(MASTER_TEST_PASSWORD);
        final String secret = "secret";
        final String encryptedSecret = encryptionTestee.encrypt(secret);
        final SecretsEncryption decryptionTestee = new SecretsEncryption("Wrong password");

        final String decryptedSecret = decryptionTestee.decrypt(encryptedSecret);

        assertNull(decryptedSecret);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Add secret that should be encrypted:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String secret = reader.readLine();
        System.out.println("Enter the master password:");
        final String masterPassword = reader.readLine();
        final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
        final String encryptedSecret = secretsEncryption.encrypt(secret);
        System.out.println("The encrpyted secret is: " + encryptedSecret);

        final SecretsEncryption secretsDecryption = new SecretsEncryption(masterPassword);
        final String decryptedSecret = secretsDecryption.decrypt(encryptedSecret);
        System.out.println("The decrypted secret is: " + decryptedSecret);
    }
}