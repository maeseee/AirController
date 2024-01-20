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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

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
    @Mock
    private AirQualityChecker airQualityChecker;

    @ParameterizedTest(name = "{index} => mainFreshOn={0}, hourlyFreshAirOn={1}, humidityFreshAirOn={2}, shouldFreshAirBeOn={3}")
    @ArgumentsSource(FreshAirArgumentProvider.class)
    void testFreshAirControllerOutputWhenSensorsAreAvailable(boolean mainFreshOn, boolean hourlyFreshAirOn, boolean humidityFreshAirOn,
                                                             boolean shouldFreshAirBeOn) {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(mainFreshOn);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(hourlyFreshAirOn);
        final SensorValues sensorValues = mock(SensorValues.class);
        when(humidityExchanger.turnFreshAirOn(sensorValues)).thenReturn(humidityFreshAirOn);
        final AirController testee =
                new AirController(controlledVentilationSystem, sensorValues, dailyFreshAir, hourlyFreshAir, humidityExchanger, airQualityChecker);

        testee.run();

        verify(controlledVentilationSystem).setAirFlowOn(shouldFreshAirBeOn);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
        verify(sensorValues).invalidateSensorValuesIfNeeded(any());
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
    void testWhenUpdateIndoorAirValueTheFirstTimeThenIgnoreHumidityController() {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        final AirValue indoorAirValue = mock(AirValue.class);
        final SensorValues sensorValues = new SensorValues(indoorAirValue, null);
        final AirController testee =
                new AirController(controlledVentilationSystem, sensorValues, dailyFreshAir, hourlyFreshAir, humidityExchanger, airQualityChecker);

        testee.run();

        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void testWhenUpdateOutdoorAirValueTheFirstTimeThenIgnoreHumidityController() {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        final AirValue outdoorAirValue = mock(AirValue.class);
        final SensorValues sensorValues = new SensorValues(null, outdoorAirValue);
        final AirController testee =
                new AirController(controlledVentilationSystem, sensorValues, dailyFreshAir, hourlyFreshAir, humidityExchanger, airQualityChecker);

        testee.run();

        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void testWhenFreshAirOffThenHumidityExchangerOff() {
        when(dailyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        when(hourlyFreshAir.turnFreshAirOn(any())).thenReturn(false);
        final SensorValues sensorValues = mock(SensorValues.class);
        when(humidityExchanger.turnHumidityExchangerOn(sensorValues)).thenReturn(true);
        final AirController testee =
                new AirController(controlledVentilationSystem, sensorValues, dailyFreshAir, hourlyFreshAir, humidityExchanger, airQualityChecker);

        testee.run();

        verify(humidityExchanger).turnHumidityExchangerOn(sensorValues);
        verify(controlledVentilationSystem).setAirFlowOn(false);
        verify(controlledVentilationSystem).setHumidityExchangerOn(false);
        verify(sensorValues).invalidateSensorValuesIfNeeded(any());
    }
}