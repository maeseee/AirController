package org.airController.util;

import com.pi4j.util.StringUtil;

import java.io.IOException;
import java.util.Optional;

public class EnvironmentVariable {

    public static Optional<String> readEnvironmentVariable(String variableName){
        final String variableContent = System.getenv(variableName);
        return StringUtil.isNotNullOrEmpty(variableContent) ? Optional.of(variableContent) : Optional.empty();
    }

    public static void main(String[] args) throws IOException {
        final Optional<String> apiKey = EnvironmentVariable.readEnvironmentVariable("weather_api_key");
        System.out.println(apiKey);
    }
}
