package org.air_controller.gpio.dingtian_relay;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DingtianResponseInterpreterTest {

    @ParameterizedTest
    @CsvSource({
            "&0&4&0&0&0&0&, false, false, false, false",
            "&0&4&1&0&0&0&, true, false, false, false",
            "&0&4&0&1&0&0&, false, true, false, false",
            "&0&4&0&0&1&0&, false, false, true, false",
            "&0&4&0&0&0&1&, false, false, false, true",
            "&0&4&1&1&1&1&, true, true, true, true",
    })
    void shouldInterpretRelayState(String response, boolean expectedRelay1State, boolean expectedRelay2State, boolean expectedRelay3State,
            boolean expectedRelay4State) {
        final DingtianResponseInterpreter testee = new DingtianResponseInterpreter();

        final List<Boolean> relayStates = testee.interpretRelayState(response);

        assertThat(relayStates).hasSize(4);
        assertThat(relayStates.get(0)).isEqualTo(expectedRelay1State);
        assertThat(relayStates.get(1)).isEqualTo(expectedRelay2State);
        assertThat(relayStates.get(2)).isEqualTo(expectedRelay3State);
        assertThat(relayStates.get(3)).isEqualTo(expectedRelay4State);
    }

    @Test
    void shouldFail_whenResultInvalid() {
        final DingtianResponseInterpreter testee = new DingtianResponseInterpreter();

        final List<Boolean> relayStates = testee.interpretRelayState("&1&4&0&0&0&0&");

        assertThat(relayStates).isEmpty();
    }

    @Test
    void shouldRead5Relays_whenHavingThem() {
        final DingtianResponseInterpreter testee = new DingtianResponseInterpreter();

        final List<Boolean> relayStates = testee.interpretRelayState("&0&5&0&0&0&0&0&");

        assertThat(relayStates).hasSize(5);
    }

    @Test
    void shouldFail_whenNumberOfRelaysDoNotMatchNumberOfParameters() {
        final DingtianResponseInterpreter testee = new DingtianResponseInterpreter();

        final List<Boolean> relayStates = testee.interpretRelayState("&0&5&0&0&0&0&");

        assertThat(relayStates).isEmpty();
    }
}