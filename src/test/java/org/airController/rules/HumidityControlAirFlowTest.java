package org.airController.rules;

import org.airController.sensorValues.CurrentSensorValues;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.Temperature;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumidityControlAirFlowTest {

    @Mock
    private CurrentSensorValues sensorValues;

    @ParameterizedTest(name = "{index} => humidity %={0}, expectedResult={1}")
    @CsvSource({
            "52.5, 0.0",
            "65, 1.0",
            "70, 1.0",
            "40, -1.0",
            "39, -1.0"
    })
    void shouldCalculateHumidityPercentage_whenOutdoorHumidityIsBelowIndoor(double indoorHumidity, double expectedResult)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(22.0);
        final Humidity humidity = Humidity.createFromRelative(indoorHumidity, temperature);
        when(sensorValues.getIndoorTemperature()).thenReturn(Optional.of(temperature));
        when(sensorValues.getIndoorHumidity()).thenReturn(Optional.of(humidity));
        when(sensorValues.isIndoorHumidityAboveOutdoorHumidity()).thenReturn(true);
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(sensorValues);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, Offset.offset(0.01));
    }

    @ParameterizedTest(name = "{index} => humidity %={0}, expectedResult={1}")
    @CsvSource({
            "52.5, 0.0",
            "65, -1.0",
            "70, -1.0",
            "40, 1.0",
            "39, 1.0"
    })
    void shouldCalculateHumidityPercentage_whenOutdoorHumidityIsAboveIndoor(double indoorHumidity, double expectedResult)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(22.0);
        final Humidity humidity = Humidity.createFromRelative(indoorHumidity, temperature);
        when(sensorValues.getIndoorTemperature()).thenReturn(Optional.of(temperature));
        when(sensorValues.getIndoorHumidity()).thenReturn(Optional.of(humidity));
        when(sensorValues.isIndoorHumidityAboveOutdoorHumidity()).thenReturn(false);
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(sensorValues);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, Offset.offset(0.01));
    }

    @Test
    void shouldReturn0_whenTemperatureValueNotAvailable() {
        when(sensorValues.getIndoorTemperature()).thenReturn(Optional.empty());
        when(sensorValues.getIndoorHumidity()).thenReturn(Optional.empty());
        final HumidityControlAirFlow testee = new HumidityControlAirFlow(sensorValues);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isEqualTo(0.0);
    }
}