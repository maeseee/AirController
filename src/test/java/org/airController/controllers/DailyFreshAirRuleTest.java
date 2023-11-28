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
            "2023-08-13 02:34, true"
    })
    void testDates(String dateTimeString, boolean expectedResult) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        final DailyFreshAirRule testee = new DailyFreshAirRule();

        final boolean freshAirOn = testee.turnFreshAirOn(dateTime);

        assertEquals(expectedResult, freshAirOn);
    }

}