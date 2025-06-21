package org.air_controller.rules;

import org.air_controller.system.SystemStatistics;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodicallyAirFlowTest {

    @Mock
    private SystemStatistics statistics;

    @ParameterizedTest(name = "{index} => onDuration={0}, expectedResult={1}")
    @CsvSource({
            "0, 0.4",
            "120, -0.4",
            "60, 0"
    })
    void shouldReturnPercentage(int onDuration, double expectedResult) {
        when(statistics.getOnDurationOfLastTwoHours()).thenReturn(Duration.ofMinutes(onDuration));
        final PeriodicallyAirFlow testee = new PeriodicallyAirFlow(statistics);

        final Confidence airFlowNeed = testee.turnOnConfidence();

        assertThat(airFlowNeed.getWeightedConfidenceValue()).isCloseTo(expectedResult, within(0.01));
    }
}