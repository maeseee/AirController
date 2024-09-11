package org.airController.rules;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PercentageTest {

    @ParameterizedTest
    @CsvSource({
            "-1.1, 0.5",
            "-0.5, 1.1",
            "0.5, -0.5",
    })
    void shouldThrow_whenInvalidBounds(double lowerBound, double upperBound) {
        assertThrows(IllegalArgumentException.class, () -> new Percentage(0.5, lowerBound, upperBound));
    }

    @ParameterizedTest
    @CsvSource({
            "-1.0, 0.5",
            "-0.5, 1.0",
            "0.5, 0.5",
    })
    void shouldNotThrow_whenValidBoundCornerCases(double lowerBound, double upperBound) {
        assertDoesNotThrow(() -> new Percentage(0.5, lowerBound, upperBound));
    }

    @ParameterizedTest
    @CsvSource({
            "-1.0, -1.0",
            "-1.1, -1.0",
            "1.0, 1.0",
            "1.1, 1.0",
            "0.5, 0.5",
    })
    void shouldBoundToPercentageLimits(double percentage, double expectedPercentage) {
        Percentage result = new Percentage(percentage);

        assertThat(result.getPercentage()).isEqualTo(expectedPercentage);
    }

    @ParameterizedTest
    @CsvSource({
            "-0.5, -0.5",
            "-0.6, -0.5",
            "0.5, 0.5",
            "1.1, 0.5",
            "0, 0",
    })
    void shouldBoundPercentageToBounds(double percentage, double expectedPercentage) {
        Percentage result = new Percentage(percentage, -0.5, 0.5);

        assertThat(result.getPercentage()).isEqualTo(expectedPercentage);
    }
}