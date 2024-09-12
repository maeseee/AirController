package org.airController.rules;

import org.airController.controllers.SensorValues;
import org.airController.entities.CarbonDioxide;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CO2ControlAirFlowTest {

    @Mock
    SensorValues sensorValues;

    @ParameterizedTest(name = "{index} => co2 ppm={0}, expectedResult={1}")
    @CsvSource({
            "600, -1.0",
            "1200, 1.0",
            "1500, 1.0",
            "900, 0.0"
    })
    void shouldCalculateCo2Percentage(double co2, double expectedResult) throws IOException {
        Optional<CarbonDioxide> carbonDioxide = Optional.of(CarbonDioxide.createFromPpm(co2));
        when(sensorValues.getIndoorCo2()).thenReturn(carbonDioxide);
        CO2ControlAirFlow testee = new CO2ControlAirFlow(sensorValues);

        Percentage result = testee.getAirFlowNeed();

        assertThat(result.getPercentage()).isEqualTo(expectedResult);
    }
}