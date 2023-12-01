package org.airController.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyFreshAirRuleTest {

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @CsvSource({
            "2023-11-28 22:58, false",
            "2023-10-20 12:58, false",
            "2023-12-24 14:58, true",
            "2023-07-24 14:58, false",
            "2023-08-05 23:45, false",
            "2023-08-13 02:34, true",
            "2023-01-01 17:30, false",
            "2023-01-01 13:00, false",
            "2023-01-01 13:01, true",
            "2023-01-01 16:30, true",
            "2023-08-01 07:30, false",
            "2023-08-01 01:00, false",
            "2023-08-01 02:01, true",
            "2023-08-01 05:30, true"
    })
    void testDates(String dateTimeString, boolean expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        final DailyFreshAir testee = new DailyFreshAir();

        final boolean freshAirOn = testee.turnFreshAirOn(dateTime);

        assertEquals(expectedResult, freshAirOn);
    }

}