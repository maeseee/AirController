package org.airController.controllers;

import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.InvaildArgumentException;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentSensorDataTest {
    @Test
    void shouldReturnEmpty_whenValuesMissing() {
        final CurrentSensorData testee = new CurrentSensorData();

        final Optional<Temperature> temperature = testee.getTemperature();
        final Optional<Temperature> humidity = testee.getTemperature();
        final Optional<Temperature> co2 = testee.getTemperature();

        assertThat(temperature).isNotPresent();
        assertThat(humidity).isNotPresent();
        assertThat(co2).isNotPresent();
    }

    @Test
    void shouldReturnTemperature() throws InvaildArgumentException {
        final CurrentSensorData testee = new CurrentSensorData();
        final Temperature temperature = Temperature.createFromCelsius(20.0);

        testee.setTemperature(temperature);
        final Optional<Temperature> result = testee.getTemperature();

        assertThat(result).isPresent().hasValue(temperature);
    }

    @Test
    void shouldReturnHumidity() throws InvaildArgumentException {
        final CurrentSensorData testee = new CurrentSensorData();
        final Humidity humidity = Humidity.createFromAbsolute(10.0);

        testee.setHumidity(humidity);
        final Optional<Humidity> result = testee.getHumidity();

        assertThat(result).isPresent().hasValue(humidity);
    }

    @Test
    void shouldReturnCo2() throws InvaildArgumentException {
        final CurrentSensorData testee = new CurrentSensorData();
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(600);

        testee.setCo2(co2);
        final Optional<CarbonDioxide> result = testee.getCo2();

        assertThat(result).isPresent().hasValue(co2);
    }
}