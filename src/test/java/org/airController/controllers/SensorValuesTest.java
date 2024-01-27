package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SensorValuesTest {

    @Test
    void testWhenUpdateIndoorAirValueThenUseNewValue() throws IOException {
        final AirValue airValue = new AirValue(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(50.0));
        final SensorValues testee = new SensorValues();

        testee.updateIndoorAirValue(airValue);

        assertEquals(airValue, testee.getIndoorAirValue());
    }

    @Test
    void testWhenUpdateOutdoorAirValueThenUseNewValue() throws IOException {
        final AirValue airValue = new AirValue(Temperature.createFromCelsius(-11.0), Humidity.createFromRelative(99.0));
        final SensorValues testee = new SensorValues();

        testee.updateOutdoorAirValue(airValue);

        assertEquals(airValue, testee.getOutdoorAirValue());
    }

    @Test
    void testWhen4HoursPassedThenInvalidateSensorValues() {
        final AirValue airValue = mock(AirValue.class);
        final LocalDateTime nowMinus5Hours = LocalDateTime.now().minusHours(5);
        when(airValue.getTime()).thenReturn(nowMinus5Hours);
        final SensorValues testee = new SensorValues();

        testee.updateIndoorAirValue(airValue);
        testee.updateOutdoorAirValue(airValue);
        testee.invalidateSensorValuesIfNeeded();

        assertNull(testee.getIndoorAirValue());
        assertNull(testee.getOutdoorAirValue());
    }
}