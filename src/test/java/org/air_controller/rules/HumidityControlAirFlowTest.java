package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentClimateDataPoint;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.DataPointBuilder;
import org.junit.jupiter.api.Test;
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
class HumidityControlAirFlowTest {

    @Mock
    private CurrentClimateDataPoint currentIndoorDataPoint;
    @Mock
    private CurrentClimateDataPoint currentOutdoorDataPoint;

    @ParameterizedTest(name = "{index} => indoorHumidity={0}%, outdoorHumidity={1}%, expectedResult={2}")
    @CsvSource({
            "52.5, 52.5, 0.0",
            "52.5, 65.0, 0.0",
            "65.0, 65.0, 0.0",
            "65.0, 52.5, 1.0",
            "70.0, 52.5, 1.0",
            "40.0, 52.5, 1.0",
            "40.0, 65.0, 1.0",
            "40.0, 27.5, -1.0",
            "65.0, 77.5, -1.0",
    })
    void shouldCalculateHumidityPercentage(double relativeIndoorHumidity, double relativeOutdoorHumidity,
            double expectedResult)
            throws InvalidArgumentException {
        final ClimateDataPoint indoorClimateDataPoint = new DataPointBuilder()
                .setTemperatureCelsius(22.5)
                .setHumidityRelative(relativeIndoorHumidity)
                .build();
        final ClimateDataPoint outdoorClimateDataPoint = new DataPointBuilder()
                .setTemperatureCelsius(22.5)
                .setHumidityRelative(relativeOutdoorHumidity)
                .build();
        when(currentIndoorDataPoint.getCurrentClimateDataPoint()).thenReturn(Optional.of(indoorClimateDataPoint));
        when(currentOutdoorDataPoint.getCurrentClimateDataPoint()).thenReturn(Optional.of(outdoorClimateDataPoint));
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(currentIndoorDataPoint, currentOutdoorDataPoint);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, within(0.01));
    }

    @Test
    void shouldReturn0_whenCurrentDataPointNotAvailable() {
        when(currentIndoorDataPoint.getCurrentClimateDataPoint()).thenReturn(Optional.empty());
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(currentIndoorDataPoint, currentOutdoorDataPoint);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isEqualTo(0.0);
    }
}