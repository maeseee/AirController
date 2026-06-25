package org.air_controller.rules.airflow;

import org.air_controller.rules.Confidence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.Mockito.mockStatic;

class DailyTest {

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @CsvSource({
            "2023-07-24 04:00 Z, 1.0",
            "2023-07-24 07:00 Z, 0.707",
            "2023-07-24 10:00 Z, 0.0",
            "2023-07-24 13:00 Z, -0.707",
            "2023-07-24 16:00 Z, -1.0",
            "2023-07-24 19:00 Z, -0.707",
            "2023-07-24 22:00 Z, 0.0",
            "2023-07-24 01:00 Z, 0.707",
            "2023-01-24 04:00 Z, -1.0",
            "2023-01-24 10:00 Z, 0.0",
            "2023-01-24 22:00 Z, 0.0"
    })
    void shouldCalculateFreshAirConfidenceAccordingToTime(String dateTimeString, double expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm X");
        final ZonedDateTime dateTime = ZonedDateTime.parse(dateTimeString, formatter);
        final Daily testee = new Daily();
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class)) {
            mocked.when(() -> ZonedDateTime.now(ZoneOffset.UTC)).thenReturn(dateTime);

            final Confidence result = testee.turnOnConfidence();

            assertThat(result.value()).isCloseTo(expectedResult * Daily.CONFIDENCE_WEIGHT, within(0.01));
        }
    }

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @CsvSource({
            "2023-05-25 04:00 Z, 0.2", // Total 75 days
            "2023-06-09 04:00 Z, 0.4",
            "2023-06-24 04:00 Z, 0.6",
            "2023-07-09 04:00 Z, 0.8",
            "2023-07-24 04:00 Z, 1.0",
            "2023-08-08 04:00 Z, 0.75", // Total 60 days
            "2023-08-23 04:00 Z, 0.5",
            "2023-09-07 04:00 Z, 0.25",
            "2023-09-22 04:00 Z, 0.0",
            "2023-10-23 04:00 Z, -0.25", // Total 124 days
            "2023-11-23 04:00 Z, -0.5",
            "2023-12-24 04:00 Z, -0.75",
            "2023-01-24 04:00 Z, -1.0",
            "2023-03-18 04:00 Z, -0.5", // Total 106 days
            "2023-05-10 04:00 Z, 0.0",

    })
    void shouldCalculateFreshAirConfidenceAccordingToSeason(String dateTimeString, double expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm X");
        final ZonedDateTime dateTime = ZonedDateTime.parse(dateTimeString, formatter);
        final Daily testee = new Daily();
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class)) {
            mocked.when(() -> ZonedDateTime.now(ZoneOffset.UTC)).thenReturn(dateTime);

            final Confidence result = testee.turnOnConfidence();

            assertThat(result.value()).isCloseTo(expectedResult * Daily.CONFIDENCE_WEIGHT, within(0.01));
        }
    }

    @Test
    void shouldReturnName() {
        final Daily testee = new Daily();

        final String name = testee.name();

        Assertions.assertThat(name).isEqualTo("Daily air flow control");
    }
}