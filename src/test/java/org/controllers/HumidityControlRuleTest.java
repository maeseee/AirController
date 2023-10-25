package org.controllers;

import org.entities.AirVO;
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
            final Temperature temperature = Temperature.createFromCelsius(23);
            final Humidity humidity = Humidity.createFromRelative(50);
            final Humidity humidityLow = Humidity.createFromRelative(40);
            final Humidity humidityLowLow = Humidity.createFromRelative(30);
            final Humidity humidityHigh = Humidity.createFromRelative(60);
            final Humidity humidityHighHigh = Humidity.createFromRelative(70);
            return Stream.of(Arguments.of(new IndoorAirValues(temperature, humidityLow), new OutdoorAirValues(temperature, humidityHigh), false),
                    Arguments.of(new IndoorAirValues(temperature, humidityHigh), new OutdoorAirValues(temperature, humidityLow), false),
                    Arguments.of(new IndoorAirValues(temperature, humidityLowLow), new OutdoorAirValues(temperature, humidityLow), false),
                    Arguments.of(new IndoorAirValues(temperature, humidityLow), new OutdoorAirValues(temperature, humidityLowLow), true),
                    Arguments.of(new IndoorAirValues(temperature, humidityHighHigh), new OutdoorAirValues(temperature, humidityHigh), false),
                    Arguments.of(new IndoorAirValues(temperature, humidityHigh), new OutdoorAirValues(temperature, humidityHighHigh), true),
                    Arguments.of(new IndoorAirValues(null), new OutdoorAirValues(null), false),
                    Arguments.of(new IndoorAirValues(null), new OutdoorAirValues(new AirVO(temperature, humidity)), false),
                    Arguments.of(new IndoorAirValues(new AirVO(temperature, humidity)), new OutdoorAirValues(null), false));
        }
    }

}