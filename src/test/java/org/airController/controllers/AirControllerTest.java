package org.airController.controllers;

import org.airController.sensorAdapter.SensorValue;
import org.airController.system.ControlledVentilationSystemImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirControllerTest {

    @Captor
    ArgumentCaptor<SensorValue> sensorValueArgumentCaptor;

    @ParameterizedTest(
            name = "{index} => mainFreshOn={0}, hourlyFreshAirOn={1}, shouldFreshAirBeOn={2}")
    @ArgumentsSource(FreshAirArgumentProvider.class)
    void testFreshAirControllerOutput(boolean mainFreshOn, boolean hourlyFreshAirOn, boolean shouldFreshAirBeOn) {
        final ControlledVentilationSystemImpl controlledVentilationSystem = mock(ControlledVentilationSystemImpl.class);
        final DailyFreshAirRule dailyFreshAirRule = mock(DailyFreshAirRule.class);
        final HourlyFreshAirRule hourlyFreshAirRule = mock(HourlyFreshAirRule.class);
        final HumidityExchangerControlRule humidityExchangerControlRule = mock(HumidityExchangerControlRule.class);
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(mainFreshOn);
        lenient().when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(hourlyFreshAirOn);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(false);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule,
                        humidityExchangerControlRule);

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
        final DailyFreshAirRule dailyFreshAirRule = mock(DailyFreshAirRule.class);
        final HourlyFreshAirRule hourlyFreshAirRule = mock(HourlyFreshAirRule.class);
        final HumidityExchangerControlRule humidityExchangerControlRule = mock(HumidityExchangerControlRule.class);
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule,
                        humidityExchangerControlRule);

        testee.runOneLoop();

        verify(controlledVentilationSystem).setAirFlowOn(true);
        verify(controlledVentilationSystem).setHumidityExchangerOn(true);
    }

    @Test
    void testWhenUpdateIndoorSensorValueThenUseNewValue() {
        final ControlledVentilationSystemImpl controlledVentilationSystem = mock(ControlledVentilationSystemImpl.class);
        final DailyFreshAirRule dailyFreshAirRule = mock(DailyFreshAirRule.class);
        final HourlyFreshAirRule hourlyFreshAirRule = mock(HourlyFreshAirRule.class);
        final HumidityExchangerControlRule humidityExchangerControlRule = mock(HumidityExchangerControlRule.class);
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final SensorValue sensorValue = mock(SensorValue.class);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule,
                        humidityExchangerControlRule);

        testee.updateIndoorSensorValue(sensorValue);

        verify(humidityExchangerControlRule).turnHumidityExchangerOn(sensorValueArgumentCaptor.capture(), eq(null));
        final SensorValue sensorValueCature = sensorValueArgumentCaptor.getValue();
        assertEquals(sensorValue, sensorValueCature);
    }

    @Test
    void testWhenUpdateOutdoorSensorValueThenUseNewValue() {
        final ControlledVentilationSystemImpl controlledVentilationSystem = mock(ControlledVentilationSystemImpl.class);
        final DailyFreshAirRule dailyFreshAirRule = mock(DailyFreshAirRule.class);
        final HourlyFreshAirRule hourlyFreshAirRule = mock(HourlyFreshAirRule.class);
        final HumidityExchangerControlRule humidityExchangerControlRule = mock(HumidityExchangerControlRule.class);
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final SensorValue sensorValue = mock(SensorValue.class);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule,
                        humidityExchangerControlRule);

        testee.updateOutdoorSensorValue(sensorValue);

        verify(humidityExchangerControlRule).turnHumidityExchangerOn(eq(null), sensorValueArgumentCaptor.capture());
        final SensorValue sensorValueCature = sensorValueArgumentCaptor.getValue();
        assertEquals(sensorValue, sensorValueCature);
    }

}