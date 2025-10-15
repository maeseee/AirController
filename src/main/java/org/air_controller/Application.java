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
import java.util.concurrent.*;

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

    Application(Sensors sensors, RuleApplier ruleApplier, DailyOnTimeLogger statistics, SystemStateLogger systemStateLogger,
            ScheduledExecutorService executor) {
        this.sensors = sensors;
        this.ruleApplier = ruleApplier;
        this.statistics = statistics;
        this.executor = executor;
        this.systemStateLogger = systemStateLogger;
    }

    public void run() {
        addThreadExecutorWithTimeout("Outdoorsensor", sensors.outdoor(), Duration.ZERO, SENSOR_READ_PERIOD);
        addThreadExecutorWithTimeout("Indoorsensor", sensors.indoor(), Duration.ZERO, SENSOR_READ_PERIOD);
        addThreadExecutorWithTimeout("RuleApplier", ruleApplier, Duration.ZERO, RULE_APPLIER_PERIOD);
        addThreadExecutorWithTimeout("SystemStateLogger", systemStateLogger, SENSOR_READ_PERIOD.dividedBy(2), LOG_PERIOD);

        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime midnight = ZonedDateTime.of(now.toLocalDate().atStartOfDay().plusDays(1), ZoneOffset.UTC);
        final Duration initialDelay = Duration.between(now, midnight).plusSeconds(1);
        addThreadExecutorWithTimeout("Statistics", statistics, initialDelay, Duration.ofDays(1));

        logger.info("All setup and running...");
    }

    private void addThreadExecutorWithTimeout(String taskName, Runnable command, Duration initialDelay, Duration period) {
        executor.scheduleAtFixedRate(() -> runTask(taskName, command), initialDelay.toSeconds(), period.toSeconds(), TimeUnit.SECONDS);
    }

    private void runTask(String taskName, Runnable command) {
        final int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try (ExecutorService service = Executors.newSingleThreadExecutor()) {
                Future<?> future = service.submit(command);
                try {
                    future.get(120, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    retryCount++;
                    logger.error("Scheduled task {} timed out! RetryCount={}\n{}", taskName, retryCount, e.getMessage());
                    future.cancel(true);
                } catch (Exception e) {
                    retryCount++;
                    logger.error("Task {} failed!  RetryCount={}\n{}", taskName, retryCount, e.getMessage());
                }
            }
        }
    }
}