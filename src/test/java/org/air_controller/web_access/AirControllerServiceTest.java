package org.air_controller.web_access;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirControllerServiceTest {

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;

    @Test
    void shouldGetOnPercentageFromTheLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> actions = List.of(
                new SystemAction(now.minusHours(10), OutputState.ON),
                new SystemAction(now.minusHours(9), OutputState.OFF)
        );
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(actions);
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, null, null);

        final double percentage = testee.getOnPercentageFromTheLast24Hours();

        assertThat(percentage).isCloseTo(1.0 / 12.0, within(0.1));
    }
}