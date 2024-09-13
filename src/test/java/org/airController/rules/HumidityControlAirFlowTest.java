package org.airController.rules;

import org.airController.controllers.SensorValues;
import org.airController.entities.Humidity;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumidityControlAirFlowTest {

    @Mock
    private SensorValues sensorValues;

    @ParameterizedTest(name = "{index} => humidity %={0}, expectedResult={1}")
    @CsvSource({
            "52, 0.0",
            "60, 1.0",
            "64, 1.0",
            "44, -1.0",
            "40, -1.0",
    })
    void shouldCalculateHumidityPercentage_whenOutdoorHumidityIsBelowIndoor(double indoorHumidity, double expectedResult) throws IOException {
        Humidity humidity = Humidity.createFromRelative(indoorHumidity);
        when(sensorValues.getIndoorHumidity()).thenReturn(humidity);
        when(sensorValues.isIndoorHumidityAboveOutdoorHumidity()).thenReturn(true);
        HumidityControlAirFlow testee = new HumidityControlAirFlow(sensorValues);

        Percentage result = testee.turnOn();

        assertThat(result.getPercentage()).isCloseTo(expectedResult, Offset.offset(0.01));
    }

    @ParameterizedTest(name = "{index} => humidity %={0}, expectedResult={1}")
    @CsvSource({
            "52, 0.0",
            "60, -1.0",
            "64, -1.0",
            "44, 1.0",
            "40, 1.0",
    })
    void shouldCalculateHumidityPercentage_whenOutdoorHumidityIsAboveIndoor(double indoorHumidity, double expectedResult) throws IOException {
        Humidity humidity = Humidity.createFromRelative(indoorHumidity);
        when(sensorValues.getIndoorHumidity()).thenReturn(humidity);
        when(sensorValues.isIndoorHumidityAboveOutdoorHumidity()).thenReturn(false);
        HumidityControlAirFlow testee = new HumidityControlAirFlow(sensorValues);

        Percentage result = testee.turnOn();

        assertThat(result.getPercentage()).isCloseTo(expectedResult, Offset.offset(0.01));
    }
}