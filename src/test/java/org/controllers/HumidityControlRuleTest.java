package org.controllers;

import org.entities.AirValues;
import org.entities.Humidity;
import org.entities.Temperature;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sensors.IndoorAirValues;
import org.sensors.OutdoorAirValues;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HumidityControlRuleTest {

    @ParameterizedTest(name = "{index} => indoor={0}, outdoor={1}, expectedResult={2}")
    @ArgumentsSource(HumidityControlArgumentProvider.class)
    void testHumidityControlRule(IndoorAirValues indoorAirValues, OutdoorAirValues outdoorAirValues, boolean expectedResult) {
        final HumidityControlRule testee = new HumidityControlRule();

        final boolean result = testee.turnHumidityExchangerOn(indoorAirValues, outdoorAirValues);

        assertEquals(expectedResult, result);
    }

    static class HumidityControlArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final Temperature temperature = new Temperature(23);
            final Humidity humidity = new Humidity(50);
            return Stream.of(
                    Arguments.of(new IndoorAirValues(temperature, new Humidity(40)), new OutdoorAirValues(new Humidity(60), temperature), false),
                    Arguments.of(new IndoorAirValues(temperature, new Humidity(60)), new OutdoorAirValues(new Humidity(40), temperature), false),
                    Arguments.of(new IndoorAirValues(temperature, new Humidity(30)), new OutdoorAirValues(new Humidity(40), temperature), false),
                    Arguments.of(new IndoorAirValues(temperature, new Humidity(40)), new OutdoorAirValues(new Humidity(30), temperature), true),
                    Arguments.of(new IndoorAirValues(temperature, new Humidity(70)), new OutdoorAirValues(new Humidity(60), temperature), false),
                    Arguments.of(new IndoorAirValues(temperature, new Humidity(60)), new OutdoorAirValues(new Humidity(70), temperature), true),
                    Arguments.of(new IndoorAirValues(null), new OutdoorAirValues(null), false),
                    Arguments.of(new IndoorAirValues(null), new OutdoorAirValues(new AirValues(temperature, humidity)), false),
                    Arguments.of(new IndoorAirValues(new AirValues(temperature, humidity)), new OutdoorAirValues(null), false)
            );
        }
    }

}