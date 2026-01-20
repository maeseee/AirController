package org.air_controller.system_action;

import org.air_controller.rules.Confidence;
import org.air_controller.system.OutputState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class VentilationSystemPersistenceDataTest {

    @Test
    void shouldReturnConfidenceString_whenHavingOneEnty() {
        final OutputState outputState = OutputState.ON;
        final Map<String, Confidence> confidences = new HashMap<>();
        confidences.put("MyConfidence", new Confidence(0.5, 0.5));
        final VentilationSystemPersistenceData testee =
                new VentilationSystemPersistenceData(outputState, 0.0, confidences, ZonedDateTime.now());

        final String confidencesText = testee.getConfidencesText();

        assertThat(confidencesText).isEqualTo("MyConfidence: 0.25");
    }
}
