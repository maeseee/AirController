package org.controllers;

import org.entities.AirValues;
import org.entities.Temperature;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.outputsystem.AirControllingSystem;
import org.sensors.IndoorAirValues;
import org.sensors.OutdoorAirValues;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AirControllerTest {

    @ParameterizedTest(
            name = "{index} => mainFreshOn={0}, hourlyFreshAirOn={1}, humidityExchangeOn={2}, shouldFreshAirBeOn={3}, shouldHumidityExchangeBeOn={4}")
    @ArgumentsSource(AirControllerArgumentProvider.class)
    void testAirController(boolean mainFreshOn, boolean hourlyFreshAirOn, boolean humidityExchangeOn, boolean shouldFreshAirBeOn,
                           boolean shouldHumidityExchangeBeOn) {
        final AirValues airValues = new AirValues(23, 50);
        final IndoorAirValues indoorAirValues = mock(IndoorAirValues.class);
        when(indoorAirValues.getAirValues()).thenReturn(airValues);
        final OutdoorAirValues outdoorAirValues = mock(OutdoorAirValues.class);
        when(outdoorAirValues.getAirValues()).thenReturn(airValues);
        final AirControllingSystem airControllingSystem = mock(AirControllingSystem.class);
        final MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule = mock(MainFreshAirTimeSlotRule.class);
        final HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule = mock(HourlyFreshAirTimeSlotRule.class);
        final HumidityControlRule humidityControlRule = mock(HumidityControlRule.class);
        when(mainFreshAirTimeSlotRule.turnFreshAirOn(any())).thenReturn(mainFreshOn);
        when(hourlyFreshAirTimeSlotRule.turnFreshAirOn(any())).thenReturn(hourlyFreshAirOn);
        when(humidityControlRule.turnHumidityExchangerOn(indoorAirValues, outdoorAirValues)).thenReturn(humidityExchangeOn);
        final AirController testee =
                new AirController(indoorAirValues, outdoorAirValues, airControllingSystem, mainFreshAirTimeSlotRule, hourlyFreshAirTimeSlotRule,
                        humidityControlRule);

        testee.runOneLoop();

        verify(airControllingSystem).setAirFlowOn(shouldFreshAirBeOn);
        verify(airControllingSystem).setHumidityExchangerOn(shouldHumidityExchangeBeOn);
    }

    static class AirControllerArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            final Temperature temperature = new Temperature(23);
            return Stream.of(
                    Arguments.of(false, false, false, false, false),
                    Arguments.of(true, false, false, true, false),
                    Arguments.of(false, true, false, true, false),
                    Arguments.of(false, false, true, true, true),
                    Arguments.of(true, false, true, true, true),
                    Arguments.of(false, true, true, true, true)
            );
        }
    }
}