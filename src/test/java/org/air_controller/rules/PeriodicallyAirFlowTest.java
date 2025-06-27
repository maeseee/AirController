package org.air_controller.rules;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodicallyAirFlowTest {

    @Mock
    private SystemActionDbAccessor dbAccessor;

    @ParameterizedTest(name = "{index} => onDuration={0}, expectedResult={1}")
    @CsvSource({
            "0, 0.4",
            "120, -0.4",
            "60, 0"
    })
    void shouldReturnPercentage(int onDuration, double expectedResult) {
        final ZonedDateTime endTime = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> systemActions = new ArrayList<>();
        systemActions.add(new SystemAction(endTime.minusMinutes(onDuration), OutputState.ON));
        systemActions.add(new SystemAction(endTime, OutputState.OFF));
        when(dbAccessor.getActionsFromTimeToNow(any())).thenReturn(systemActions);
        final PeriodicallyAirFlow testee = new PeriodicallyAirFlow(dbAccessor);

        final Confidence airFlowNeed = testee.turnOnConfidence();

        assertThat(airFlowNeed.getWeightedConfidenceValue()).isCloseTo(expectedResult, within(0.01));
    }
}