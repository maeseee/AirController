package org.airController.rules;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ConfidentTest {

    @ParameterizedTest(name = "{index} => confidentValue={0}")
    @CsvSource({
            "-1.1",
            "1.1",
            "10.0",
    })
    void shouldThrow_whenInvalidConfidentValue(double confidentValue) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Confident(confidentValue));
    }

    @ParameterizedTest(name = "{index} => confidentValue={0}, weight={1}, expectedWeightedValue{2}")
    @CsvSource({
            "1.0, 1.0, 1.0",
            "-1.0, -1.0, 1.0",
            "1.0, 0.5, 0.5",
            "1.0, 2.0, 2.0",
            "0.0, 1.0, 0.0"
    })
    void shouldBoundToPercentageLimits(double confidentValue, double weight, double expectedWeightedValue) {
        final Confident result = new Confident(confidentValue, weight);

        assertThat(result.getWeightedConfidentValue()).isCloseTo(expectedWeightedValue, Offset.offset(0.001));
    }
}