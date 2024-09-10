package org.airController.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodicallyAirFlowTest {

    @Mock
    Timetraker timetraker;

    @Test
    void shouldReturnMax_when0OnTime() {
        when(timetraker.getAirFlowOnDurationInLastHour()).thenReturn(Duration.ofMinutes(0));
        PeriodicallyAirFlow testee = new PeriodicallyAirFlow(timetraker);

        Percentage airFlowNeed = testee.getAirFlowNeed();

        assertThat(airFlowNeed.getPercentage()).isEqualTo(0.5);
    }

    @Test
    void shouldReturnMin_when20OnTime() {
        when(timetraker.getAirFlowOnDurationInLastHour()).thenReturn(Duration.ofMinutes(20));
        PeriodicallyAirFlow testee = new PeriodicallyAirFlow(timetraker);

        Percentage airFlowNeed = testee.getAirFlowNeed();

        assertThat(airFlowNeed.getPercentage()).isEqualTo(-0.5);
    }
}