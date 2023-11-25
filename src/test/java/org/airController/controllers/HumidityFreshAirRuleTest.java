package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HumidityFreshAirRuleTest {

    @ParameterizedTest(name = "{index} => indoor={0}, outdoor={1}, expectedResult={2}")
    @ArgumentsSource(HumidityFreshAirArgumentProvider.class)
    void testHumidityFreshAirRule(Humidity indoorHumidity, Humidity outdoorHumidity, boolean expectedResult) {
        final Temperature temperature = Temperature.createFromCelsius(23);
        final AirValue indoorAirValue = new AirValue(temperature, indoorHumidity);
        final AirValue outdoorAirValue = new AirValue(temperature, outdoorHumidity);
        final HumidityFreshAirRule testee = new HumidityFreshAirRule();

        final boolean result = testee.turnFreshAirOn(indoorAirValue, outdoorAirValue);

        assertEquals(expectedResult, result);
    }

    static class HumidityFreshAirArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            final Humidity humidityLow = Humidity.createFromRelative(40);
            final Humidity humidityLowLow = Humidity.createFromRelative(30);
            final Humidity humidityHigh = Humidity.createFromRelative(60);
            final Humidity humidityHighHigh = Humidity.createFromRelative(70);
            return Stream.of(
                    Arguments.of(humidityLow, humidityHigh, true),
                    Arguments.of(humidityHigh, humidityLow, true),
                    Arguments.of(humidityLowLow, humidityLow, false),
                    Arguments.of(humidityLow, humidityLowLow, false),
                    Arguments.of(humidityHighHigh, humidityHigh, false),
                    Arguments.of(humidityHigh, humidityHighHigh, false));
        }
    }

}