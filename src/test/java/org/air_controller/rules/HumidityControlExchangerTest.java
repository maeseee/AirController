package org.air_controller.rules;

import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor_values.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumidityControlExchangerTest {

    @Mock
    private ClimateSensor indoor;
    @Mock
    private ClimateSensor outdoor;

    @ParameterizedTest(name = "{index} => indoorHumidity={0}%, outdoorHumidity={1}%, expectedResult={2}")
    @CsvSource({
            "52.0, 65.0, -1.0",
            "60.0, 52.5, -1.0",
            "45.0, 52.5, -1.0",
            "45.0, 65.0, -1.0",
            "40.0, 27.5, 1.0",
            "65.0, 77.5, 1.0",
    })
    void shouldControlHumidityExchanger(double relativeIndoorHumidity, double relativeOutdoorHumidity, double expectedResult)
            throws InvalidArgumentException {
        final ClimateDataPoint indoorClimateDataPoint = new DataPointBuilder()
                .setTemperatureCelsius(22.0)
                .setHumidityRelative(relativeIndoorHumidity)
                .build();
        final ClimateDataPoint outdoorClimateDataPoint = new DataPointBuilder()
                .setTemperatureCelsius(22.0)
                .setHumidityRelative(relativeOutdoorHumidity)
                .build();
        when(indoor.getCurrentDataPoint()).thenReturn(Optional.of(indoorClimateDataPoint));
        when(outdoor.getCurrentDataPoint()).thenReturn(Optional.of(outdoorClimateDataPoint));
        final ClimateSensors sensors = new ClimateSensors(indoor, outdoor);
        final HumidityControlExchanger testee = new HumidityControlExchanger(sensors);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, within(0.01));
    }
}