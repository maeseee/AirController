package org.controllers;

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
        final HumidityExchangerRule testee = new HumidityControlRule();

        final boolean result = testee.turnHumidityExchangerOn(indoorAirValues, outdoorAirValues);

        assertEquals(expectedResult, result);
    }

    static class HumidityControlArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            final Temperature temperature = new Temperature(23);
            return Stream.of(
                    Arguments.of(new IndoorAirValues(new Humidity(40), temperature), new OutdoorAirValues(new Humidity(60), temperature), false),
                    Arguments.of(new IndoorAirValues(new Humidity(60), temperature), new OutdoorAirValues(new Humidity(40), temperature), false),
                    Arguments.of(new IndoorAirValues(new Humidity(30), temperature), new OutdoorAirValues(new Humidity(40), temperature), false),
                    Arguments.of(new IndoorAirValues(new Humidity(40), temperature), new OutdoorAirValues(new Humidity(30), temperature), true),
                    Arguments.of(new IndoorAirValues(new Humidity(70), temperature), new OutdoorAirValues(new Humidity(60), temperature), false),
                    Arguments.of(new IndoorAirValues(new Humidity(60), temperature), new OutdoorAirValues(new Humidity(70), temperature), true)
            );
        }
    }

}