package org.air_controller.secrets;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EnvironmentVariable {

    public static Optional<String> readEnvironmentVariable(String variableName) {
        final String variableContent = System.getenv(variableName);
        return Strings.isNotEmpty(variableContent) ? Optional.of(variableContent) : Optional.empty();
    }
}
