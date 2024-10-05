package org.airController.sensorValues;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentSensorValuesTest {

    @Test
    void testWhenUpdateIndoorSensorDataThenUseNewData() throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity humidity = Humidity.createFromRelative(50.0, temperature);
        final SensorData sensorData = createSensorData(temperature, humidity);

        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorData(sensorData);

        assertThat(testee.getIndoorTemperature()).isPresent().hasValue(temperature);
        assertThat(testee.getIndoorHumidity()).isPresent().hasValue(humidity);
    }

    @Test
    void testWhenInitializedThenHaveInvalidSensorValues() {
        final CurrentSensorValues testee = new CurrentSensorValues();

        assertThat(testee.getIndoorHumidity()).isNotPresent();
        assertThat(testee.getIndoorTemperature()).isNotPresent();
        assertThat(testee.getIndoorCo2()).isNotPresent();
    }

    @Test
    void testIndoorHumidityHigher() throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity indoorHumidity = Humidity.createFromRelative(60.0, temperature);
        final SensorData indoorSensorData = createSensorData(temperature, indoorHumidity);
        final Humidity outdoorHumidity = Humidity.createFromRelative(50.0, temperature);
        final SensorData outdoorSensorData = createSensorData(temperature, outdoorHumidity);

        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorData(indoorSensorData);
        testee.updateOutdoorSensorData(outdoorSensorData);

        assertThat(testee.isIndoorHumidityAboveOutdoorHumidity()).isTrue();
        assertThat(testee.getIndoorTemperature()).isPresent().hasValue(temperature);
        assertThat(testee.getIndoorHumidity()).isPresent().hasValue(indoorHumidity);
    }

    @Test
    void testIndoorHumidityLower() throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity indoorHumidity = Humidity.createFromRelative(40.0, temperature);
        final SensorData indoorSensorData = createSensorData(temperature, indoorHumidity);
        final Humidity outdoorHumidity = Humidity.createFromRelative(50.0, temperature);
        final SensorData outdoorSensorData = createSensorData(temperature, outdoorHumidity);
        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorData(indoorSensorData);
        testee.updateOutdoorSensorData(outdoorSensorData);

        assertThat(testee.isIndoorHumidityAboveOutdoorHumidity()).isFalse();
        assertThat(testee.getIndoorTemperature()).isPresent().hasValue(temperature);
        assertThat(testee.getIndoorHumidity()).isPresent().hasValue(indoorHumidity);
    }

    private static SensorData createSensorData(Temperature temperature, Humidity humidity) {
        return new SensorDataImpl(temperature, humidity, null, LocalDateTime.now());
    }
}