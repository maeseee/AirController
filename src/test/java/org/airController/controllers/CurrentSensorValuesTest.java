package org.airController.controllers;

import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvaildArgumentException;
import org.airController.sensorValues.SensorData;
import org.airController.sensorValues.Temperature;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrentSensorValuesTest {

    @Test
    void testWhenUpdateIndoorSensorDataThenUseNewData() throws InvaildArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity humidity = Humidity.createFromRelative(50.0, temperature);
        final SensorData sensorData = mock(SensorData.class);
        when(sensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(sensorData.getHumidity()).thenReturn(Optional.of(humidity));

        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorData(sensorData);

        assertThat(testee.getIndoorHumidity()).isPresent();
        assertThat(testee.getIndoorHumidity().get()).isEqualTo(humidity);
    }

    @Test
    void testWhenInitializedThenHaveInvalidSensorValues() {
        final CurrentSensorValues testee = new CurrentSensorValues();

        assertThat(testee.getIndoorHumidity()).isNotPresent();
        assertThat(testee.getIndoorTemperature()).isNotPresent();
        assertThat(testee.getIndoorCo2()).isNotPresent();
    }

    @Test
    void testIndoorHumidityHigher() throws InvaildArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity indoorHumidity = Humidity.createFromRelative(60.0, temperature);
        final SensorData indoorSensorData = mock(SensorData.class);
        when(indoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(indoorSensorData.getHumidity()).thenReturn(Optional.of(indoorHumidity));
        final Humidity outdoorHumidity = Humidity.createFromRelative(50.0, temperature);
        final SensorData outdoorSensorData = mock(SensorData.class);
        when(outdoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(outdoorSensorData.getHumidity()).thenReturn(Optional.of(outdoorHumidity));

        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorData(indoorSensorData);
        testee.updateOutdoorSensorData(outdoorSensorData);

        assertThat(testee.isIndoorHumidityAboveOutdoorHumidity()).isTrue();
        assertThat(testee.getIndoorHumidity()).isEqualTo(Optional.of(indoorHumidity));
        assertThat(testee.getIndoorTemperature()).isEqualTo(Optional.of(temperature));
    }

    @Test
    void testIndoorHumidityLower() throws InvaildArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity indoorHumidity = Humidity.createFromRelative(40.0, temperature);
        final SensorData indoorSensorData = mock(SensorData.class);
        when(indoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(indoorSensorData.getHumidity()).thenReturn(Optional.of(indoorHumidity));
        final Humidity outdoorHumidity = Humidity.createFromRelative(50.0, temperature);
        final SensorData outdoorSensorData = mock(SensorData.class);
        when(outdoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(outdoorSensorData.getHumidity()).thenReturn(Optional.of(outdoorHumidity));
        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorData(indoorSensorData);
        testee.updateOutdoorSensorData(indoorSensorData);

        assertThat(testee.isIndoorHumidityAboveOutdoorHumidity()).isFalse();
    }
}