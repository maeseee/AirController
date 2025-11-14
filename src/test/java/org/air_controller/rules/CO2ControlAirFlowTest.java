package org.air_controller.rules;

import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
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
    private ClimateSensor indoor;

    @ParameterizedTest(name = "{index} => co2 ppm={0}, expectedConfidence={1}")
    @CsvSource({
            "400, -1.0",
            "1000, 1.0",
            "1400, 1.0",
            "700, 0.0"
    })
    void shouldCalculateCo2Confidence(double co2, double expectedConfidence) throws InvalidArgumentException {
        final ClimateDataPoint dataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(21.0)
                .setHumidityRelative(50.0)
                .setCo2(co2)
                .setTime(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
        when(indoor.getCurrentDataPoint()).thenReturn(Optional.of(dataPoint));
        final CO2ControlAirFlow testee = new CO2ControlAirFlow(indoor);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isEqualTo(expectedConfidence);
    }
}