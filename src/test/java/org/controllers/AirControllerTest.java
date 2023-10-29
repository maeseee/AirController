package org.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.system.ControlledVentilationSystemImpl;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AirControllerTest {

    @ParameterizedTest(
            name = "{index} => mainFreshOn={0}, hourlyFreshAirOn={1}, shouldFreshAirBeOn={2}")
    @ArgumentsSource(FreshAirArgumentProvider.class)
    void testFreshAirControllerOutput(boolean mainFreshOn, boolean hourlyFreshAirOn, boolean shouldFreshAirBeOn) {
        final ControlledVentilationSystemImpl controlledVentilationSystem = mock(ControlledVentilationSystemImpl.class);
        final MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule = mock(MainFreshAirTimeSlotRule.class);
        final HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule = mock(HourlyFreshAirTimeSlotRule.class);
        final HumidityControlRule humidityControlRule = mock(HumidityControlRule.class);
        when(mainFreshAirTimeSlotRule.turnFreshAirOn(any())).thenReturn(mainFreshOn);
        when(hourlyFreshAirTimeSlotRule.turnFreshAirOn(any())).thenReturn(hourlyFreshAirOn);
        when(humidityControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(false);
        final AirController testee =
                new AirController(controlledVentilationSystem, mainFreshAirTimeSlotRule, hourlyFreshAirTimeSlotRule,
                        humidityControlRule);
        testee.runOneLoop();

        verify(controlledVentilationSystem).setAirFlowOn(shouldFreshAirBeOn);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    static class FreshAirArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(false, false, false),
                    Arguments.of(false, true, true),
                    Arguments.of(true, false, true),
                    Arguments.of(true, true, true)
            );
        }
    }

    @Test
    void testWhenHumidityExchangeOnThenHumidityExchangerAndAirFlowOn() {
        final ControlledVentilationSystemImpl controlledVentilationSystem = mock(ControlledVentilationSystemImpl.class);
        final MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule = mock(MainFreshAirTimeSlotRule.class);
        final HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule = mock(HourlyFreshAirTimeSlotRule.class);
        final HumidityControlRule humidityControlRule = mock(HumidityControlRule.class);
        when(mainFreshAirTimeSlotRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirTimeSlotRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirController testee =
                new AirController(controlledVentilationSystem, mainFreshAirTimeSlotRule, hourlyFreshAirTimeSlotRule,
                        humidityControlRule);

        testee.runOneLoop();

        verify(controlledVentilationSystem).setAirFlowOn(true);
        verify(controlledVentilationSystem).setHumidityExchangerOn(true);
    }
}