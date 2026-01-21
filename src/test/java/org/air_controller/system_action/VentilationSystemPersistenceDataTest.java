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

    @Test
    void shouldReturnConfidenceString_whenHavingMultipleEntries() {
        final OutputState outputState = OutputState.ON;
        final Map<String, Confidence> confidences = new HashMap<>();
        confidences.put("MyConfidence", new Confidence(0.5, 0.5));
        confidences.put("Con2", new Confidence(0.2, 0.2));
        final VentilationSystemPersistenceData testee =
                new VentilationSystemPersistenceData(outputState, 0.0, confidences, ZonedDateTime.now());

        final String confidencesText = testee.getConfidencesText();

        assertThat(confidencesText).isEqualTo("MyConfidence: 0.25,  Con2: 0.04");
    }

    @Test
    void shouldConvertToConfidenceMap() {
        final String confidencesString = "MyConfidence: 0.25,  Con2: 0.04";

        final Map<String, Confidence> confidencesMap = VentilationSystemPersistenceData.toConfidencesMap(confidencesString);

        assertThat(confidencesMap).hasSize(2);
        assertThat(confidencesMap.get("MyConfidence")).isEqualTo(new Confidence(0.25, 1.0));
        assertThat(confidencesMap.get("Con2")).isEqualTo(new Confidence(0.04, 1.0));
    }
}
