package org.air_controller.statistics;

import org.air_controller.rules.Rule;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class SystemStateLoggerTest {

    @Mock
    private VentilationSystem ventilationSystem;
    @Mock
    private List<Rule> freshAirRules;

    @Test
    void shouldLogMessageOfCurrentState(CapturedOutput output) {
        when(ventilationSystem.isAirFlowOn()).thenReturn(OutputState.ON);
        final SystemStateLogger testee = new SystemStateLogger(ventilationSystem, freshAirRules);

        testee.run();

        assertThat(output.getOut())
                .contains("Fresh air state changed to ON because of the confidence score 0.00");
    }
}