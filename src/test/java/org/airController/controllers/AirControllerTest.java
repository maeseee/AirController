package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirControllerTest {

    @Mock
    private ControlledVentilationSystemImpl controlledVentilationSystem;
    @Mock
    private DailyFreshAirRule dailyFreshAirRule;
    @Mock
    private HourlyFreshAirRule hourlyFreshAirRule;
    @Mock
    private HumidityExchangerControlRule humidityExchangerControlRule;
    @Mock
    private AirValue airValue;

    @Captor
    ArgumentCaptor<AirValue> airValueArgumentCaptor;

    @ParameterizedTest(
            name = "{index} => mainFreshOn={0}, hourlyFreshAirOn={1}, shouldFreshAirBeOn={2}")
    @ArgumentsSource(FreshAirArgumentProvider.class)
    void testFreshAirControllerOutputWhenSensorsAreAvailable(boolean mainFreshOn, boolean hourlyFreshAirOn, boolean shouldFreshAirBeOn) {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(mainFreshOn);
        lenient().when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(hourlyFreshAirOn);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule, humidityExchangerControlRule, null);

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
    void testWhenHumidityExchangeOffThenAndAirFlowOn() {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(false);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule, humidityExchangerControlRule, airValue);

        testee.runOneLoop();

        verify(controlledVentilationSystem).setAirFlowOn(true);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void testWhenUpdateIndoorSensorValueThenUseNewValue() {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirValue indoorAirValue = mock(AirValue.class);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule, humidityExchangerControlRule, airValue);

        testee.updateIndoorSensorValue(indoorAirValue);

        verify(humidityExchangerControlRule).turnHumidityExchangerOn(airValueArgumentCaptor.capture(), any());
        final AirValue indoorAirValueCature = airValueArgumentCaptor.getValue();
        assertEquals(indoorAirValue, indoorAirValueCature);
        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void testWhenUpdateOutdoorSensorValueThenUseNewValue() {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirValue outdoorAirValue = mock(AirValue.class);
        final AirController testee =
                new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule, humidityExchangerControlRule, airValue);

        testee.updateOutdoorSensorValue(outdoorAirValue);

        verify(humidityExchangerControlRule).turnHumidityExchangerOn(any(), airValueArgumentCaptor.capture());
        final AirValue outdoorAirValueCature = airValueArgumentCaptor.getValue();
        assertEquals(outdoorAirValue, outdoorAirValueCature);
        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }
}