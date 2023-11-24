package org.airController.controllers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HourlyFreshAirTimeSlotRuleTest {

    @ParameterizedTest(name = "{index} => dateTime={0}, expectedResult={1}")
    @ArgumentsSource(HourlyFreshAirArgumentProvider.class)
    void testHourlyFreshAirRule(LocalTime time, boolean expectedResult) {
        final HourlyFreshAirRule testee = new HourlyFreshAirRule();

        final boolean result = testee.turnFreshAirOn(time);

        assertEquals(expectedResult, result);
    }

    static class HourlyFreshAirArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(LocalTime.of(1, 1, 1), true),
                    Arguments.of(LocalTime.of(13, 1, 1), true),
                    Arguments.of(LocalTime.of(5, 11, 1), false),
                    Arguments.of(LocalTime.of(17, 11, 1), false)
            );
        }
    }

}