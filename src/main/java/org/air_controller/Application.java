package org.air_controller;

import org.air_controller.rules.RuleApplier;
import org.air_controller.rules.TimeKeeper;
import org.air_controller.sensor.Sensors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static final int OUTDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int INDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int RULE_APPLIER_PERIOD_MINUTES = 1;

    private final Sensors sensors;
    private final RuleApplier ruleApplier;
    private final TimeKeeper timeKeeper;
    private final ScheduledExecutorService executor;

    Application(Sensors sensors, RuleApplier ruleApplier, TimeKeeper timeKeeper, ScheduledExecutorService executor) {
        this.sensors = sensors;
        this.ruleApplier = ruleApplier;
        this.timeKeeper = timeKeeper;
        this.executor = executor;
    }

    public void run() {
        executor.scheduleAtFixedRate(sensors.outdoor(), 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(sensors.indoor(), 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(ruleApplier, 0, RULE_APPLIER_PERIOD_MINUTES, TimeUnit.MINUTES);

        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime midnight = ZonedDateTime.of(now.toLocalDate().atStartOfDay().plusDays(1), ZoneOffset.UTC);
        final long initialDelay = Duration.between(now, midnight).plusSeconds(1).toSeconds();
        executor.scheduleAtFixedRate(timeKeeper, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        logger.info("All setup and running...");
    }
}