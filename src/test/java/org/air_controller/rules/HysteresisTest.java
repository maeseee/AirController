package org.air_controller.rules;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HysteresisTest {

    @ParameterizedTest
    @CsvSource({
            "0.0, false, false",
            "0.0, true, true",
            "2.0, false, true",
            "-2.0, true, false",
    })
    void shouldUseHysteresis(double confidence, boolean currentlyOn, boolean expectOnState) {
        Hysteresis hysteresis = new Hysteresis(1.0);

        boolean changeState = hysteresis.changeStateWithHysteresis(confidence, currentlyOn);

        assertThat(changeState).isEqualTo(expectOnState);
    }
}