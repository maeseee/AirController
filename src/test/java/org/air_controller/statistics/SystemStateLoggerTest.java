package org.air_controller.statistics;

import org.air_controller.rules.Rule;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.test.appender.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemStateLoggerTest {

    private ListAppender listAppender;
    private org.apache.logging.log4j.core.Logger rootLogger;
    private Level originalRootLogLevel;

    @Mock
    private VentilationSystem ventilationSystem;
    @Mock
    private List<Rule> freshAirRules;

    @BeforeEach
    public void setUp() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        rootLogger = ctx.getRootLogger();
        originalRootLogLevel = rootLogger.getLevel();
        rootLogger.setLevel(Level.INFO);
        listAppender = new ListAppender("Logger");
        listAppender.start();
        rootLogger.addAppender(listAppender);
        ctx.updateLoggers();
    }

    @AfterEach
    void tearDown() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        rootLogger.removeAppender(listAppender);
        listAppender.stop();
        rootLogger.setLevel(originalRootLogLevel);
        ctx.updateLoggers();
    }

    @Test
    void shouldLogMessageOfCurrentState() {
        when(ventilationSystem.isAirFlowOn()).thenReturn(OutputState.ON);
        final SystemStateLogger testee = new SystemStateLogger(ventilationSystem, freshAirRules);

        testee.run();

        final List<LogEvent> events = listAppender.getEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().getMessage().getFormattedMessage())
                .isEqualTo("Fresh air state changed to ON because of the confidence score 0.00\n");
    }

}