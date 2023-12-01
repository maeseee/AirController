package org.airController.util;

import java.io.IOException;
import java.util.Optional;

class EnvironmentVariableTest {

    public static void main(String[] args) throws IOException {
        final Optional<String> apiKey = EnvironmentVariable.readEnvironmentVariable("weather_api_key");
        System.out.println(apiKey);
    }

}