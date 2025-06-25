package org.air_controller.rules;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.Mockito.mockStatic;

class DailyAirFlowTest {

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @CsvSource({
            "2023-07-24 02:00 Z, 1.0",
            "2023-07-24 05:00 Z, 0.707",
            "2023-07-24 08:00 Z, 0.0",
            "2023-07-24 11:00 Z, -0.707",
            "2023-07-24 14:00 Z, -1.0",
            "2023-07-24 17:00 Z, -0.707",
            "2023-07-24 20:00 Z, 0.0",
            "2023-07-24 23:00 Z, 0.707",
            "2023-01-24 02:00 Z, -1.0",
            "2023-01-24 08:00 Z, 0.0",
            "2023-01-24 20:00 Z, 0.0"
    })
    void shouldCalculateFreshAirConfidenceAccordingToTime(String dateTimeString, double expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm X");
        final ZonedDateTime dateTime = ZonedDateTime.parse(dateTimeString, formatter);
        final DailyAirFlow testee = new DailyAirFlow();
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class)) {
            mocked.when(() -> ZonedDateTime.now(ZoneOffset.UTC)).thenReturn(dateTime);

            final Confidence result = testee.turnOnConfidence();

            assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult * 0.6, within(0.01));
        }
    }

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @CsvSource({
            "2023-05-10 02:00 Z, 0.0",
            "2023-05-20 02:00 Z, 0.5",
            "2023-05-30 02:00 Z, 1",
            "2023-04-30 02:00 Z, -0.5",
            "2023-04-20 02:00 Z, -1.0",
            "2023-09-21 02:00 Z, 0.0",
            "2023-10-01 02:00 Z, -0.5",
            "2023-10-11 02:00 Z, -1.0",
            "2023-09-11 02:00 Z, 0.5",
            "2023-09-01 02:00 Z, 1.0"
    })
    void shouldCalculateFreshAirConfidenceAccordingToSeason(String dateTimeString, double expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm X");
        final ZonedDateTime dateTime = ZonedDateTime.parse(dateTimeString, formatter);
        final DailyAirFlow testee = new DailyAirFlow();
        try (MockedStatic<ZonedDateTime> mocked = mockStatic(ZonedDateTime.class)) {
            mocked.when(() -> ZonedDateTime.now(ZoneOffset.UTC)).thenReturn(dateTime);

            final Confidence result = testee.turnOnConfidence();

            assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult * 0.6, within(0.01));
        }
    }
}