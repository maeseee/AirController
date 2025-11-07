package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.SensorData;
import org.air_controller.sensor_values.SensorDataBuilder;
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
    private CurrentSensorData currentIndoorSensorData;
    @Mock
    private CurrentSensorData currentOutdoorSensorData;

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
        final SensorData indoorSensorData = new SensorDataBuilder()
                .setTemperatureCelsius(22.5)
                .setHumidityRelative(relativeIndoorHumidity)
                .build();
        final SensorData outdoorSensorData = new SensorDataBuilder()
                .setTemperatureCelsius(22.5)
                .setHumidityRelative(relativeOutdoorHumidity)
                .build();
        when(currentIndoorSensorData.getCurrentSensorData()).thenReturn(Optional.of(indoorSensorData));
        when(currentOutdoorSensorData.getCurrentSensorData()).thenReturn(Optional.of(outdoorSensorData));
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, within(0.01));
    }

    @Test
    void shouldReturn0_whenCurrentSensorDataNotAvailable() {
        when(currentIndoorSensorData.getCurrentSensorData()).thenReturn(Optional.empty());
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isEqualTo(0.0);
    }
}