package org.air_controller.sensor.open_weather_api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

@DisabledIfEnvironmentVariable(named = "GITHUB_ACTIONS", matches = "true")
class OpenWeatherApiIntegrationTest {

    @Test
    void shouldCallOpenWeatherApi() {
        final OpenWeatherApiSensor testee = new OpenWeatherApiSensor();

        final String response = testee.readData();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();
    }
}