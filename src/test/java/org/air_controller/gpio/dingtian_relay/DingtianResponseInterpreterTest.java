package org.air_controller.gpio.dingtian_relay;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

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

        final Optional<DingtianRelayState> relayStateOptional
                = testee.interpretRelayState(response);

        assertThat(relayStateOptional).isPresent();
        final DingtianRelayState relayState = relayStateOptional.get();
        assertThat(relayState.relay1On()).isEqualTo(expectedRelay1State);
        assertThat(relayState.relay2On()).isEqualTo(expectedRelay2State);
        assertThat(relayState.relay3On()).isEqualTo(expectedRelay3State);
        assertThat(relayState.relay4On()).isEqualTo(expectedRelay4State);
    }

    @Test
    void shouldFail_whenResultInvalid() {
        final DingtianResponseInterpreter testee = new DingtianResponseInterpreter();

        final Optional<DingtianRelayState> relayStateOptional
                = testee.interpretRelayState("&1&4&0&0&0&0&");

        assertThat(relayStateOptional).isEmpty();
    }

    @Test
    void shouldFail_whenHavingMoreThan4Relays() {
        final DingtianResponseInterpreter testee = new DingtianResponseInterpreter();

        final Optional<DingtianRelayState> relayStateOptional
                = testee.interpretRelayState("&0&5&0&0&0&0&0&");

        assertThat(relayStateOptional).isEmpty();
    }
}