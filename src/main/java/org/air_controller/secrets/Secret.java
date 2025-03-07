package org.air_controller.secrets;

import lombok.NoArgsConstructor;
import org.air_controller.sensor_data_persistence.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Secret {

    public static String getSecret(String environmentVariableName, String encryptedSecret) {
        final Optional<String> secret = EnvironmentVariable.readEnvironmentVariable(environmentVariableName);
        return secret.orElseGet(() -> getSecretFromEncryptedSecret(environmentVariableName, encryptedSecret));
    }

    private static String getSecretFromEncryptedSecret(String environmentVariableName, String encryptedSecret) {
        System.out.println("Enter the master password:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String masterPassword;
        try {
            masterPassword = reader.readLine();
        } catch (IOException e) {
            throw new ParseException("Line could not be read", e.getCause());
        }
        final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
        final String decryptedSecret = secretsEncryption.decrypt(encryptedSecret);
        if (decryptedSecret == null) {
            System.err.println("Wrong master password entered!");
            return "";
        }
        System.out.println("Secret for " + environmentVariableName + " is " + decryptedSecret);
        return decryptedSecret;
    }
}
