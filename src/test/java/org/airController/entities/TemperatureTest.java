package org.airController.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureTest {

    @Test
    void testCelsiusToCelsius() throws InvaildArgumentException {
        final Temperature testee = Temperature.createFromCelsius(0);

        final double celsius = testee.getCelsius();

        assertEquals(0, celsius);
    }

    @Test
    void testKelvinToCelsius() throws InvaildArgumentException {
        final Temperature testee = Temperature.createFromKelvin(273.15);

        final double celsius = testee.getCelsius();

        assertEquals(0, celsius);
    }

    @Test
    void testKelvinToKelvin() throws InvaildArgumentException {
        final Temperature testee = Temperature.createFromKelvin(273.15);

        final double kelvin = testee.getKelvin();

        assertEquals(273.15, kelvin);
    }

    @Test
    void testCelsiusToKelvin() throws InvaildArgumentException {
        final Temperature testee = Temperature.createFromCelsius(0);

        final double kelvin = testee.getKelvin();

        assertEquals(273.15, kelvin);
    }

}