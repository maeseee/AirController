package org.airController.controllers;

import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrentSensorValuesTest {

    @Test
    void testWhenUpdateIndoorSensorValueThenUseNewValue() throws IOException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity humidity = Humidity.createFromRelative(50.0);
        final SensorData sensorData = mock(SensorData.class);
        when(sensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(sensorData.getHumidity()).thenReturn(Optional.of(humidity));

        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorValue(sensorData);

        assertThat(testee.getIndoorHumidity()).isPresent();
        assertThat(testee.getIndoorHumidity().get()).isEqualTo(humidity);
    }

    @Test
    void testWhenInitializedThenHaveInvalidSensorValues() {
        final CurrentSensorValues testee = new CurrentSensorValues();

        assertThat(testee.getIndoorHumidity()).isNotPresent();
    }

    @Test
    void testIndoorHumidityHigher() throws IOException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity indoorHumidity = Humidity.createFromRelative(60.0);
        final SensorData indoorSensorData = mock(SensorData.class);
        when(indoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(indoorSensorData.getHumidity()).thenReturn(Optional.of(indoorHumidity));
        final Humidity outdoorHumidity = Humidity.createFromRelative(50.0);
        final SensorData outdoorSensorData = mock(SensorData.class);
        when(outdoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(outdoorSensorData.getHumidity()).thenReturn(Optional.of(outdoorHumidity));

        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorValue(indoorSensorData);
        testee.updateOutdoorSensorValue(outdoorSensorData);

        assertThat(testee.isIndoorHumidityAboveOutdoorHumidity()).isTrue();
    }

    @Test
    void testIndoorHumidityLower() throws IOException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity indoorHumidity = Humidity.createFromRelative(40.0);
        final SensorData indoorSensorData = mock(SensorData.class);
        when(indoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(indoorSensorData.getHumidity()).thenReturn(Optional.of(indoorHumidity));
        final Humidity outdoorHumidity = Humidity.createFromRelative(50.0);
        final SensorData outdoorSensorData = mock(SensorData.class);
        when(outdoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(outdoorSensorData.getHumidity()).thenReturn(Optional.of(outdoorHumidity));

        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorValue(indoorSensorData);
        testee.updateOutdoorSensorValue(indoorSensorData);

        assertThat(testee.isIndoorHumidityAboveOutdoorHumidity()).isFalse();
    }
}