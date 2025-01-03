package org.air_controller.secrets;

import com.pi4j.util.StringUtil;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class EnvironmentVariable {

    public static Optional<String> readEnvironmentVariable(String variableName) {
        final String variableContent = System.getenv(variableName);
        return StringUtil.isNotNullOrEmpty(variableContent) ? Optional.of(variableContent) : Optional.empty();
    }
}
