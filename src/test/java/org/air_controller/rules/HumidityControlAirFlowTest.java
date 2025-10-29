package org.air_controller.rules;

import org.air_controller.sensor_values.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
        final SensorData indoorSensorData = new SensorData(temperature, indoorHumidity, Optional.empty(), ZonedDateTime.now(ZoneOffset.UTC));
        final SensorData outdoorSensorData = new SensorData(temperature, outdoorHumidity, Optional.empty(), ZonedDateTime.now(ZoneOffset.UTC));
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