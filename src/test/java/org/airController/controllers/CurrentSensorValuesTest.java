package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrentSensorValuesTest {

    @Test
    void testWhenUpdateIndoorSensorValueThenUseNewValue() throws IOException {
        Humidity humidity = Humidity.createFromRelative(50.0);
        final AirValue airValue = new AirValue(Temperature.createFromCelsius(23.0), humidity);
        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorValue(airValue);

        assertThat(testee.getIndoorHumidity()).isPresent();
        assertThat(testee.getIndoorHumidity().get()).isEqualTo(humidity);
    }

    @Test
    void testWhenSensorInvalidThenInvalidateSensorValues() {
        final AirValue airValue = mock(AirValue.class);
        when(airValue.isSensorValid()).thenReturn(false);
        final CurrentSensorValues testee = new CurrentSensorValues();

        testee.updateIndoorSensorValue(airValue);
        testee.updateOutdoorSensorValue(airValue);

        assertThat(testee.getIndoorHumidity()).isNotPresent();
    }

    @Test
    void testWhenInitializedThenHaveInvalidSensorValues() {
        final CurrentSensorValues testee = new CurrentSensorValues();

        assertThat(testee.getIndoorHumidity()).isNotPresent();
    }
}