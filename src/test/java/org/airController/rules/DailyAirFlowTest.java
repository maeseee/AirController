package org.airController.rules;

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
            "2023-07-20 16:00, -1.0",
            "2023-07-20 10:00, 0.0",
            "2023-07-20 22:00, 0.0",
            "2023-07-20 00:00, 0.5",
            "2023-07-20 08:00, 0.5",
            "2023-01-20 04:00, -1.0",
            "2023-01-20 16:00, 1.0",
            "2023-01-20 10:00, 0.0",
            "2023-01-20 22:00, 0.0",
            "2023-01-20 00:00, -0.5",
            "2023-01-20 08:00, -0.5"
    })
    void shouldCalculateFreshAirPercentageOnSummer(String dateTimeString, double expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        final DailyAirFlow testee = new DailyAirFlow();
        try (MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(dateTime);

            Percentage result = testee.getAirFlowNeed();

            assertThat(result.getPercentage()).isCloseTo(expectedResult, Offset.offset(0.01));
        }
    }
}