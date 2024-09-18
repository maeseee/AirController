package org.airController.system;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ControlledVentilationSystemTimeKeeperTest {

    @ParameterizedTest(name = "{index} => onMinutesBeforeNow={0}, offMinutesBeforeNow={1}, expectedResult={2}")
    @CsvSource({
            "60, 0, 60",
            "70, -10, 60",
            "70, 20, 40",
            "40, 0, 40",
    })
    void shouldReturnOnDurationInTheLastHour(int onMinutesBeforeNow, int offMinutesBeforeNow, long expectedResult) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime onTime = now.minusMinutes(onMinutesBeforeNow);
        final LocalDateTime offTime = now.minusMinutes(offMinutesBeforeNow);
        final ControlledVentilationSystemTimeKeeper testee = new ControlledVentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime)
                    .thenReturn(offTime)
                    .thenReturn(now);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
        }

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(expectedResult, Offset.offset(1L));
    }

    @Test
    void shouldCountTwoTimePeriods() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime onTime1 = now.minusMinutes(50);
        final LocalDateTime offTime1 = now.minusMinutes(40);
        final LocalDateTime onTime2 = now.minusMinutes(20);
        final LocalDateTime offTime2 = now.minusMinutes(10);
        final ControlledVentilationSystemTimeKeeper testee = new ControlledVentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime1)
                    .thenReturn(offTime1)
                    .thenReturn(onTime2)
                    .thenReturn(offTime2)
                    .thenReturn(now);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
        }

        final Duration result = testee.getAirFlowOnDurationInLastHour();

        assertThat(result.toMinutes()).isCloseTo(20, Offset.offset(1L));
    }

    @Test
    void shouldReturnReportFromYesterday() {
        final LocalDateTime onTime1 = LocalDateTime.of(2024, 9, 17, 12, 0);
        final LocalDateTime offTime1 = onTime1.plusMinutes(10);
        final ControlledVentilationSystemTimeKeeper testee = new ControlledVentilationSystemTimeKeeper();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now)
                    .thenReturn(onTime1)
                    .thenReturn(offTime1);
            testee.setAirFlowOn(true);
            testee.setAirFlowOn(false);
        }

        final Duration result = testee.getTotalAirFlowFromDay(onTime1.toLocalDate());

        assertThat(result.toMinutes()).isCloseTo(10, Offset.offset(1L));
    }
}