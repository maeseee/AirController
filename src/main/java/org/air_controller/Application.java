package org.air_controller;

import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.Sensors;
import org.air_controller.statistics.DailyOnTimeLogger;
import org.air_controller.statistics.SystemStateLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static final Duration SENSOR_READ_PERIOD = Duration.ofMinutes(10);
    private static final Duration RULE_APPLIER_PERIOD = Duration.ofMinutes(1);
    private static final Duration LOG_PERIOD = Duration.ofHours(1);

    private final Sensors sensors;
    private final RuleApplier ruleApplier;
    private final DailyOnTimeLogger statistics;
    private final ScheduledExecutorService executor;
    private final SystemStateLogger systemStateLogger;

    Application(Sensors sensors, RuleApplier ruleApplier, DailyOnTimeLogger statistics, ScheduledExecutorService executor,
            SystemStateLogger systemStateLogger) {
        this.sensors = sensors;
        this.ruleApplier = ruleApplier;
        this.statistics = statistics;
        this.executor = executor;
        this.systemStateLogger = systemStateLogger;
    }

    public void run() {
        executor.scheduleAtFixedRate(sensors.outdoor(), 0, SENSOR_READ_PERIOD.toMinutes(), TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(sensors.indoor(), 0, SENSOR_READ_PERIOD.toMinutes(), TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(ruleApplier, 0, RULE_APPLIER_PERIOD.toMinutes(), TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(systemStateLogger, SENSOR_READ_PERIOD.toMinutes() / 2, LOG_PERIOD.toMinutes(), TimeUnit.MINUTES);

        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime midnight = ZonedDateTime.of(now.toLocalDate().atStartOfDay().plusDays(1), ZoneOffset.UTC);
        final long initialDelay = Duration.between(now, midnight).plusSeconds(1).toSeconds();
        executor.scheduleAtFixedRate(statistics, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        logger.info("All setup and running...");
    }
}