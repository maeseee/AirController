package org.airController.rules;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ConfidenceTest {

    @ParameterizedTest(name = "{index} => confidenceValue={0}")
    @CsvSource({
            "-1.1",
            "1.1",
            "10.0",
    })
    void shouldThrow_whenInvalidConfidenceValue(double confidenceValue) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Confidence(confidenceValue));
    }

    @ParameterizedTest(name = "{index} => confidenceValue={0}, weight={1}, expectedWeightedValue{2}")
    @CsvSource({
            "1.0, 1.0, 1.0",
            "-1.0, -1.0, 1.0",
            "1.0, 0.5, 0.5",
            "1.0, 2.0, 2.0",
            "0.0, 1.0, 0.0"
    })
    void shouldWeightConfidence(double confidenceValue, double weight, double expectedWeightedValue) {
        final Confidence result = new Confidence(confidenceValue, weight);

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedWeightedValue, Offset.offset(0.001));
    }
}