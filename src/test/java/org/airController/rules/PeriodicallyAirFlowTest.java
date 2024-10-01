package org.airController.rules;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodicallyAirFlowTest {

    @Mock
    private TimeKeeper timekeeper;

    @ParameterizedTest(name = "{index} => onDuration={0}, expectedResult={1}")
    @CsvSource({
            "0, 1",
            "60, -1",
            "30, 0"
    })
    void shouldReturnPercentage(int onDuration, double expectedResult) {
        when(timekeeper.getAirFlowOnDurationInLastHour()).thenReturn(Duration.ofMinutes(onDuration));
        PeriodicallyAirFlow testee = new PeriodicallyAirFlow(timekeeper);

        Percentage airFlowNeed = testee.turnOn();

        assertThat(airFlowNeed.getPercentage()).isCloseTo(expectedResult, Offset.offset(0.01));
    }
}