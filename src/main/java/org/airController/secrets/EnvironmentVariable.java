package org.airController.secrets;

import com.pi4j.util.StringUtil;

import java.util.Optional;

class EnvironmentVariable {

    public static Optional<String> readEnvironmentVariable(String variableName){
        final String variableContent = System.getenv(variableName);
        return StringUtil.isNotNullOrEmpty(variableContent) ? Optional.of(variableContent) : Optional.empty();
    }
}
