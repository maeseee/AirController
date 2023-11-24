package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.sensor.SensorValueImpl;
import org.airController.sensorAdapter.SensorValue;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HumidityExchangerControlRuleTest {

    @ParameterizedTest(name = "{index} => indoor={0}, outdoor={1}, expectedResult={2}")
    @ArgumentsSource(HumidityControlArgumentProvider.class)
    void testHumidityControlRule(Humidity indoorHumidity, Humidity outdoorHumidity, boolean expectedResult) {
        final Temperature temperature = Temperature.createFromCelsius(23);
        final SensorValueImpl indoorSensorValue = new SensorValueImpl(temperature, indoorHumidity);
        final SensorValueImpl outdoorSensorValue = new SensorValueImpl(temperature, outdoorHumidity);
        final HumidityExchangerControlRule testee = new HumidityExchangerControlRule();

        final boolean result = testee.turnHumidityExchangerOn(indoorSensorValue, outdoorSensorValue);

        assertEquals(expectedResult, result);
    }

    static class HumidityControlArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            final Humidity humidityLow = Humidity.createFromRelative(40);
            final Humidity humidityLowLow = Humidity.createFromRelative(30);
            final Humidity humidityHigh = Humidity.createFromRelative(60);
            final Humidity humidityHighHigh = Humidity.createFromRelative(70);
            return Stream.of(
                    Arguments.of(humidityLow, humidityHigh, false),
                    Arguments.of(humidityHigh, humidityLow, false),
                    Arguments.of(humidityLowLow, humidityLow, false),
                    Arguments.of(humidityLow, humidityLowLow, true),
                    Arguments.of(humidityHighHigh, humidityHigh, false),
                    Arguments.of(humidityHigh, humidityHighHigh, true));
        }
    }

    @ParameterizedTest(name = "{index} => indoor={0}, outdoor={1}, expectedResult={2}")
    @ArgumentsSource(EmptySensorValuesArgumentProvider.class)
    void testEmptySensorValues(SensorValue indoorSensorValue, SensorValue outdoorSensorValue, boolean expectedResult) {
        final HumidityExchangerControlRule testee = new HumidityExchangerControlRule();

        final boolean result = testee.turnHumidityExchangerOn(indoorSensorValue, outdoorSensorValue);

        assertEquals(expectedResult, result);
    }

    static class EmptySensorValuesArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            final Temperature temperature = Temperature.createFromCelsius(23);
            final Humidity humidity = Humidity.createFromRelative(50);
            return Stream.of(
                    Arguments.of(new SensorValueImpl(null), new SensorValueImpl(null), false),
                    Arguments.of(new SensorValueImpl(null), new SensorValueImpl(new AirValue(temperature, humidity)), false),
                    Arguments.of(new SensorValueImpl(temperature, humidity), new SensorValueImpl(null), false));
        }
    }
}