package org.air_controller.system_action;

import org.air_controller.rules.Confidence;
import org.air_controller.system.OutputState;
import org.assertj.core.data.Offset;
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
        final Map<String, Double> confidences = new HashMap<>();
        confidences.put("MyConfidence", 0.5);
        final VentilationSystemPersistenceData testee =
                new VentilationSystemPersistenceData(outputState, 0.0, confidences, ZonedDateTime.now());

        final String confidencesText = testee.getConfidencesText();

        assertThat(confidencesText).isEqualTo("MyConfidence: 0.5");
    }

    @Test
    void shouldReturnConfidenceString_whenHavingMultipleEntries() {
        final OutputState outputState = OutputState.ON;
        final Map<String, Double> confidences = new HashMap<>();
        confidences.put("MyConfidence", 0.2);
        confidences.put("Con2", 0.4);
        final VentilationSystemPersistenceData testee =
                new VentilationSystemPersistenceData(outputState, 0.0, confidences, ZonedDateTime.now());

        final String confidencesText = testee.getConfidencesText();

        assertThat(confidencesText).isEqualTo("MyConfidence: 0.2,  Con2: 0.4");
    }

    @Test
    void shouldConvertToConfidenceMap() {
        final String confidencesString = "MyConfidence: 0.2,  Con2: 0.4";
        final VentilationSystemPersistenceData testee =
                VentilationSystemPersistenceData.create(OutputState.ON, 0.8, confidencesString, ZonedDateTime.now());

        final Map<String, Double> confidences = testee.confidences();

        assertThat(confidences).hasSize(2);
        assertThat(confidences.get("MyConfidence")).isCloseTo(0.2, Offset.offset(0.01));
        assertThat(confidences.get("Con2")).isCloseTo(0.4, Offset.offset(0.01));
    }
}
