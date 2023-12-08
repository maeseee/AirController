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
    private DailyFreshAir dailyFreshAir;
    @Mock
    private HourlyFreshAir hourlyFreshAir;
    @Mock
    private HumidityExchanger humidityExchanger;

    @Captor
    ArgumentCaptor<AirValue> airValueArgumentCaptor;

    @ParameterizedTest(name = "{index} => mainFreshOn={0}, hourlyFreshAirOn={1}, humidityFreshAirOn={2}, shouldFreshAirBeOn={3}")
    @ArgumentsSource(FreshAirArgumentProvider.class)
    void testFreshAirControllerOutputWhenSensorsAreAvailable(boolean mainFreshOn, boolean hourlyFreshAirOn, boolean humidityFreshAirOn,
                                                             boolean shouldFreshAirBeOn) {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(mainFreshOn);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(hourlyFreshAirOn);
        when(humidityExchanger.turnFreshAirOn(any(), any())).thenReturn(humidityFreshAirOn);
        final AirValue airValue = mock(AirValue.class);
        final AirController testee = new AirController(controlledVentilationSystem, dailyFreshAir, hourlyFreshAir, humidityExchanger);

        testee.updateIndoorAirValue(airValue);
        testee.updateOutdoorAirValue(airValue);
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
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(true);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchanger.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirValue indoorAirValue = mock(AirValue.class);
        final AirValue outdoorAirValue = mock(AirValue.class);
        final AirController testee = new AirController(controlledVentilationSystem, dailyFreshAir, hourlyFreshAir, humidityExchanger);

        testee.updateIndoorAirValue(indoorAirValue);
        testee.updateOutdoorAirValue(outdoorAirValue);
        testee.runOneLoop();

        verify(humidityExchanger).turnHumidityExchangerOn(airValueArgumentCaptor.capture(), any());
        final AirValue indoorAirValueCature = airValueArgumentCaptor.getValue();
        assertEquals(indoorAirValue, indoorAirValueCature);
        verify(controlledVentilationSystem).setAirFlowOn(true);
        verify(controlledVentilationSystem).setHumidityExchangerOn(true);
    }

    @Test
    void testWhenUpdateOutdoorAirValueThenUseNewValue() {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(true);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchanger.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirValue indoorAirValue = mock(AirValue.class);
        final AirValue outdoorAirValue = mock(AirValue.class);
        final AirController testee = new AirController(controlledVentilationSystem, dailyFreshAir, hourlyFreshAir, humidityExchanger);

        testee.updateIndoorAirValue(indoorAirValue);
        testee.updateOutdoorAirValue(outdoorAirValue);
        testee.runOneLoop();

        verify(humidityExchanger).turnHumidityExchangerOn(any(), airValueArgumentCaptor.capture());
        final AirValue outdoorAirValueCature = airValueArgumentCaptor.getValue();
        assertEquals(outdoorAirValue, outdoorAirValueCature);
        verify(controlledVentilationSystem).setAirFlowOn(true);
        verify(controlledVentilationSystem).setHumidityExchangerOn(true);
    }

    @Test
    void testWhenUpdateIndoorAirValueTheFirstTimeThenIgnoreHumidityController() {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        final AirValue indoorAirValue = mock(AirValue.class);
        final AirController testee = new AirController(controlledVentilationSystem, dailyFreshAir, hourlyFreshAir, humidityExchanger);

        testee.updateIndoorAirValue(indoorAirValue);
        testee.runOneLoop();

        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void testWhenUpdateOutdoorAirValueTheFirstTimeThenIgnoreHumidityController() {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        final AirValue outdoorAirValue = mock(AirValue.class);
        final AirController testee = new AirController(controlledVentilationSystem, dailyFreshAir, hourlyFreshAir, humidityExchanger);

        testee.updateOutdoorAirValue(outdoorAirValue);
        testee.runOneLoop();

        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void testWhenFreshAirOffThenHumidityExchangerOff() {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(humidityExchanger.turnHumidityExchangerOn(any(), any())).thenReturn(true);
        final AirValue airValue = mock(AirValue.class);
        final AirController testee = new AirController(controlledVentilationSystem, dailyFreshAir, hourlyFreshAir, humidityExchanger);

        testee.updateIndoorAirValue(airValue);
        testee.updateOutdoorAirValue(airValue);
        testee.runOneLoop();

        verify(humidityExchanger).turnHumidityExchangerOn(airValueArgumentCaptor.capture(), any());
        final AirValue indoorAirValueCature = airValueArgumentCaptor.getValue();
        assertEquals(airValue, indoorAirValueCature);
        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }
}