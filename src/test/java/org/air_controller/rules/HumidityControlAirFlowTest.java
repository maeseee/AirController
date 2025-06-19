package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.Temperature;
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
    void shouldCalculateHumidityPercentage(double indoorHumidityValue, double outdoorHumidityValue,
            double expectedResult)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(22.5);
        final Humidity indoorHumidity = Humidity.createFromRelative(indoorHumidityValue, temperature);
        final Humidity outdoorHumidity = Humidity.createFromRelative(outdoorHumidityValue, temperature);
        when(currentIndoorSensorData.getHumidity()).thenReturn(Optional.of(indoorHumidity));
        when(currentIndoorSensorData.getTemperature()).thenReturn(Optional.of(temperature));
        when(currentOutdoorSensorData.getHumidity()).thenReturn(Optional.of(outdoorHumidity));
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, within(0.01));
    }

    @Test
    void shouldReturn0_whenTemperatureValueNotAvailable() {
        when(currentIndoorSensorData.getHumidity()).thenReturn(Optional.empty());
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isEqualTo(0.0);
    }
}