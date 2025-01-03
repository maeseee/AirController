package org.air_controller.rules;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mockStatic;

class DailyAirFlowTest {

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @CsvSource({
            "2023-07-20 04:00, 1.0",
            "2023-07-20 06:30, 0.5",
            "2023-07-20 16:00, -1.0",
            "2023-07-20 10:00, 0.0",
            "2023-07-20 22:00, 0.0",
            "2023-07-20 00:00, 0.0", // only 3 hours in cosinus
            "2023-07-20 08:00, 0.0", // only 3 hours in cosinus
            "2023-01-20 04:00, -1.0",
            "2023-01-20 16:00, 1.0",
            "2023-01-20 10:00, 0.0",
            "2023-01-20 22:00, 0.0",
            "2023-01-20 00:00, 0.0", // only 3 hours in cosinus
            "2023-01-20 08:00, 0.0"  // only 3 hours in cosinus
    })
    void shouldCalculateFreshAirConfidenceAccordingToSeason(String dateTimeString, double expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        final DailyAirFlow testee = new DailyAirFlow();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(dateTime);

            final Confidence result = testee.turnOnConfidence();

            assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, Offset.offset(0.01));
        }
    }
}