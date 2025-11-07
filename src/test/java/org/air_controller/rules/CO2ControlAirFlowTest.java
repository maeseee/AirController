package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.SensorData;
import org.air_controller.sensor_values.SensorDataBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CO2ControlAirFlowTest {

    @Mock
    private CurrentSensorData currentIndoorSensorData;

    @ParameterizedTest(name = "{index} => co2 ppm={0}, expectedConfidence={1}")
    @CsvSource({
            "400, -1.0",
            "1000, 1.0",
            "1400, 1.0",
            "700, 0.0"
    })
    void shouldCalculateCo2Confidence(double co2, double expectedConfidence) throws InvalidArgumentException {
        final SensorData sensorData = new SensorDataBuilder()
                .setTemperature(21.0)
                .setRelativeHumidity(50.0)
                .setCo2(co2)
                .setTime(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
        when(currentIndoorSensorData.getCurrentSensorData()).thenReturn(Optional.of(sensorData));
        final CO2ControlAirFlow testee = new CO2ControlAirFlow(currentIndoorSensorData);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isEqualTo(expectedConfidence);
    }
}