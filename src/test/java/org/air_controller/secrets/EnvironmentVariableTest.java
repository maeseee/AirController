package org.air_controller.secrets;

import java.util.Optional;

class EnvironmentVariableTest {

    static void main() {
        final Optional<String> apiKey = EnvironmentVariable.readEnvironmentVariable("weather_api_key");
        System.out.println(apiKey);
    }

}