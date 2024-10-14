package org.airController.rules;

import org.airController.sensorValues.CarbonDioxide;
import org.airController.sensorValues.CurrentSensorData;
import org.airController.sensorValues.InvalidArgumentException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CO2ControlAirFlowTest {

    @Mock
    private CurrentSensorData currentIndoorSensorData;

    @ParameterizedTest(name = "{index} => co2 ppm={0}, expectedConfidence={1}")
    @CsvSource({
            "500, -1.0",
            "1100, 1.0",
            "1400, 1.0",
            "800, 0.0"
    })
    void shouldCalculateCo2Confidence(double co2, double expectedConfidence) throws InvalidArgumentException {
        final Optional<CarbonDioxide> carbonDioxide = Optional.of(CarbonDioxide.createFromPpm(co2));
        when(currentIndoorSensorData.getCo2()).thenReturn(carbonDioxide);
        final CO2ControlAirFlow testee = new CO2ControlAirFlow(currentIndoorSensorData);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isEqualTo(expectedConfidence);
    }
}