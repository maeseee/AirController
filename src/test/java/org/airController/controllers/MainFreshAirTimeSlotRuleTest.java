package org.airController.controllers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainFreshAirTimeSlotRuleTest {

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @ArgumentsSource(MainFreshAirArgumentProvider.class)
    void testMainFreshAirRule(LocalDateTime dateTime, boolean expectedResult) {
        final DailyFreshAirRule testee = new DailyFreshAirRule();

        final boolean result = testee.turnFreshAirOn(dateTime);

        assertEquals(expectedResult, result);
    }

    static class MainFreshAirArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(LocalDateTime.of(2023, 1, 1, 17, 30), false),
                    Arguments.of(LocalDateTime.of(2023, 1, 1, 12, 0), false),
                    Arguments.of(LocalDateTime.of(2023, 1, 1, 13, 0), true),
                    Arguments.of(LocalDateTime.of(2023, 1, 1, 16, 30), true),
                    Arguments.of(LocalDateTime.of(2023, 7, 1, 7, 30), false),
                    Arguments.of(LocalDateTime.of(2023, 7, 1, 1, 0), false),
                    Arguments.of(LocalDateTime.of(2023, 7, 1, 2, 0), true),
                    Arguments.of(LocalDateTime.of(2023, 7, 1, 6, 30), true)
            );
        }
    }
}