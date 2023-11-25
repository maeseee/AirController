package org.airController.controllers;

import org.airController.entities.AirValue;
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
    private HumidityFreshAirRule humidityFreshAirRule;
    @Mock
    private HumidityExchangerControlRule humidityExchangerControlRule;
    @Mock
    private AirValue airValue;

    @Captor
    ArgumentCaptor<AirValue> airValueArgumentCaptor;

    @ParameterizedTest(name = "{index} => mainFreshOn={0}, hourlyFreshAirOn={1}, humidityFreshAirOn={2}, shouldFreshAirBeOn={3}")
    @ArgumentsSource(FreshAirArgumentProvider.class)
    void testFreshAirControllerOutputWhenSensorsAreAvailable(boolean mainFreshOn, boolean hourlyFreshAirOn, boolean humidityFreshAirOn, boolean shouldFreshAirBeOn) {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(mainFreshOn);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(hourlyFreshAirOn);
        when(humidityFreshAirRule.turnFreshAirOn(any(), any())).thenReturn(humidityFreshAirOn);
        final AirController testee = createTesteeWithInitialSensorValues();

        testee.runOneLoop();

        verify(controlledVentilationSystem).setAirFlowOn(shouldFreshAirBeOn);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    static class FreshAirArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(false, false, false, false),
                    Arguments.of(false, true, false, true),
                    Arguments.of(true, false, false, true),
                    Arguments.of(true, true, false, true),
                    Arguments.of(false, false, true, true),
                    Arguments.of(false, true, true, true),
                    Arguments.of(true, false, true, true),
                    Arguments.of(true, true, true, true)
            );
        }
    }

    @Test
    void testWhenUpdateIndoorAirValueThenUseNewValue() {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirValue indoorAirValue = mock(AirValue.class);
        final AirController testee = createTesteeWithInitialSensorValues();

        testee.updateIndoorAirValue(indoorAirValue);

        verify(humidityExchangerControlRule).turnHumidityExchangerOn(airValueArgumentCaptor.capture(), any());
        final AirValue indoorAirValueCature = airValueArgumentCaptor.getValue();
        assertEquals(indoorAirValue, indoorAirValueCature);
        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(true);
    }

    @Test
    void testWhenUpdateOutdoorAirValueThenUseNewValue() {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchangerControlRule.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirValue outdoorAirValue = mock(AirValue.class);
        final AirController testee = createTesteeWithInitialSensorValues();

        testee.updateOutdoorAirValue(outdoorAirValue);

        verify(humidityExchangerControlRule).turnHumidityExchangerOn(any(), airValueArgumentCaptor.capture());
        final AirValue outdoorAirValueCature = airValueArgumentCaptor.getValue();
        assertEquals(outdoorAirValue, outdoorAirValueCature);
        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(true);
    }

    @Test
    void testWhenUpdateIndoorAirValueTheFirstTimeThenIgnoreHumidityController() {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        final AirValue indoorAirValue = mock(AirValue.class);
        final AirController testee = createTesteeWithoutInitialSensorValues();

        testee.updateIndoorAirValue(indoorAirValue);

        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void testWhenUpdateOutdoorAirValueTheFirstTimeThenIgnoreHumidityController() {
        when(dailyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAirRule.turnFreshAirOn(any())).thenReturn(false);
        final AirValue outdoorAirValue = mock(AirValue.class);
        final AirController testee = createTesteeWithoutInitialSensorValues();

        testee.updateOutdoorAirValue(outdoorAirValue);

        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    private AirController createTesteeWithoutInitialSensorValues() {
        return new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule, humidityFreshAirRule,
                humidityExchangerControlRule, null);
    }

    private AirController createTesteeWithInitialSensorValues() {
        return new AirController(controlledVentilationSystem, dailyFreshAirRule, hourlyFreshAirRule, humidityFreshAirRule,
                humidityExchangerControlRule, airValue);
    }
}