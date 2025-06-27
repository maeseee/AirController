package org.air_controller;

import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensor.Sensors;
import org.air_controller.statistics.SystemStateLogger;
import org.air_controller.system.SystemStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    private Sensors sensors;

    @Mock
    private Sensor outdoorSensor;
    @Mock
    private Sensor indoorSensor;
    @Mock
    private RuleApplier ruleApplier;
    @Mock
    private SystemStatistics statistics;
    @Mock
    private ScheduledExecutorService executor;
    @Mock
    private SystemStateLogger systemStateLogger;

    @BeforeEach
    void setUp() {
        sensors = new Sensors(indoorSensor, outdoorSensor);
    }

    @Test
    void testWhenCreateApplicationThenScheduleExecutor() {
        final Application testee = new Application(sensors, ruleApplier, statistics, executor, systemStateLogger);

        testee.run();

        verify(executor).scheduleAtFixedRate(outdoorSensor, 0, 10, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(indoorSensor, 0, 10, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(ruleApplier, 0, 1, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(systemStateLogger, 5, 60, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(eq(statistics), anyLong(), eq(86400L), eq(TimeUnit.SECONDS));
    }
}