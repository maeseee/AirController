package org.airController.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureTest {

    @Test
    void testCelsiusToCelsius() {
        final Temperature testee = Temperature.createFromCelsius(0);

        final double celsius = testee.getCelsius();

        assertEquals(0, celsius);
    }

    @Test
    void testKelvinToCelsius() {
        final Temperature testee = Temperature.createFromKelvin(0);

        final double celsius = testee.getCelsius();

        assertEquals(-273.15, celsius);
    }

    @Test
    void testKelvinToKelvin() {
        final Temperature testee = Temperature.createFromKelvin(0);

        final double kelvin = testee.getKelvin();

        assertEquals(0, kelvin);
    }

    @Test
    void testCelsiusToKelvin() {
        final Temperature testee = Temperature.createFromCelsius(0);

        final double kelvin = testee.getKelvin();

        assertEquals(273.15, kelvin);
    }

}