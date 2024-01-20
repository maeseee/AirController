package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AirQualityCheckerTest {

    @Test
    void testWhenCo2below900ThenNoAirFlow() throws IOException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity humidity = Humidity.createFromRelative(50.0);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(700.0);
        final AirValue airValue = new AirValue(temperature, humidity, co2);
        SensorValues sensorValues = new SensorValues(airValue, null);
        final AirQualityChecker testee = new AirQualityChecker();

        final boolean airOn = testee.turnFreshAirOn(sensorValues);

        assertFalse(airOn);
    }

    @Test
    void testWhenCo2above900ThenNoAirFlow() throws IOException {
        final Temperature temperature = Temperature.createFromCelsius(23.0);
        final Humidity humidity = Humidity.createFromRelative(50.0);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(1000.0);
        final AirValue airValue = new AirValue(temperature, humidity, co2);
        SensorValues sensorValues = new SensorValues(airValue, null);
        final AirQualityChecker testee = new AirQualityChecker();

        final boolean airOn = testee.turnFreshAirOn(sensorValues);

        assertTrue(airOn);
    }
}