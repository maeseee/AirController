package org.air_controller;

import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.statistics.DailyOnTimeLogger;
import org.air_controller.statistics.SystemStateLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    private ClimateSensors sensors;

    @Mock
    private ClimateSensor outdoorSensor;
    @Mock
    private ClimateSensor indoorSensor;
    @Mock
    private RuleApplier ruleApplier;
    @Mock
    private DailyOnTimeLogger statistics;
    @Mock
    private ScheduledExecutorService executor;
    @Mock
    private SystemStateLogger systemStateLogger;

    @BeforeEach
    void setUp() {
        sensors = new ClimateSensors(indoorSensor, outdoorSensor);
    }

    @Test
    void testWhenCreateApplicationThenScheduleExecutor() {
        final Application testee = new Application(sensors, ruleApplier, statistics, systemStateLogger, executor);

        testee.run();

        verify(executor, times(2)).scheduleAtFixedRate(any(), eq(0L), eq(600L), eq(TimeUnit.SECONDS)); // outdoorSensor + indoorSensor
        verify(executor).scheduleAtFixedRate(any(), eq(0L), eq(60L), eq(TimeUnit.SECONDS)); // ruleApplier
        verify(executor).scheduleAtFixedRate(any(), eq(5 * 60L), eq(60 * 60L), eq(TimeUnit.SECONDS)); // systemStateLogger
        verify(executor).scheduleAtFixedRate(any(), anyLong(), eq(86400L), eq(TimeUnit.SECONDS)); // statistics
    }
}