package org.outputsystem;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ControlledVentilationSystemImplTest {

    @Test
    void testWhenInitalizedThenAirFlowIsOn() {
        final ControlledVentilationSystemImpl testee = new ControlledVentilationSystemImpl();

        final boolean airFlowOn = testee.isAirFlowOn();

        assertThat(airFlowOn, is(true));
    }

    @Test
    void testWhenInitalizedThenRotiIsOff() {
        final ControlledVentilationSystemImpl testee = new ControlledVentilationSystemImpl();

        final boolean result = testee.isHumidityExchangerOn();

        assertThat(result, is(false));
    }

}