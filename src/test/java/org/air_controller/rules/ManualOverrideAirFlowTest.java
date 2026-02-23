package org.air_controller.rules;

import org.air_controller.system.OutputState;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ManualOverrideAirFlowTest {

    @Test
    void shouldOverrideState_whenEventWasSent() {
        final ManualOverrideAirFlow testee = new ManualOverrideAirFlow();
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ManualOverrideEvent event = new ManualOverrideEvent(now, OutputState.ON, Duration.ofHours(1));
        testee.manualOverride(event);

        final Confidence confidence = testee.turnOnConfidence();

        assertThat(confidence.value()).isCloseTo(10.0, Offset.offset(0.001));
    }

    @Test
    void shouldFinishOverrideState_whenLastEventIsOutdated() {
        final ManualOverrideAirFlow testee = new ManualOverrideAirFlow();
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ManualOverrideEvent event = new ManualOverrideEvent(now.minusHours(2), OutputState.ON, Duration.ofHours(1));
        testee.manualOverride(event);

        final Confidence confidence = testee.turnOnConfidence();

        assertThat(confidence.value()).isCloseTo(0.0, Offset.offset(0.001));
    }
}