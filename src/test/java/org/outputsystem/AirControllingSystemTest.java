package org.outputsystem;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class AirControllingSystemTest {

    @Test
    void testWhenInitalizedThenAirFlowIsOn() {
        final AirControllingSystem testee = new AirControllingSystem();

        final boolean airFlowOn = testee.isAirFlowOn();

        assertThat(airFlowOn, is(true));
    }

    @Test
    void testWhenInitalizedThenRotiIsOff() {
        final AirControllingSystem testee = new AirControllingSystem();

        final boolean result = testee.isHumidityExchangerOn();

        assertThat(result, is(false));
    }

}