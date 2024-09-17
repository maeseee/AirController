package org.airController.system;

import org.assertj.core.data.Offset;
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
}