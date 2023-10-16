package org.outputsystem;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ControlledVentilationSystemTest {

    @Test
    void testWhenInitalizedThenAirFlowIsOn() {
        final ControlledVentilationSystem testee = new ControlledVentilationSystem();

        final boolean airFlowOn = testee.isAirFlowOn();

        assertThat(airFlowOn, is(true));
    }

    @Test
    void testWhenInitalizedThenRotiIsOff() {
        final ControlledVentilationSystem testee = new ControlledVentilationSystem();

        final boolean result = testee.isHumidityExchangerOn();

        assertThat(result, is(false));
    }

}