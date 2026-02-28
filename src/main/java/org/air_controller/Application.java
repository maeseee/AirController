package org.air_controller;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor_values.ClimateSensors;

import java.time.Duration;
import java.util.concurrent.*;

@Slf4j
public class Application {
    private static final Duration SENSOR_READ_PERIOD = Duration.ofMinutes(10);
    private static final Duration RULE_APPLIER_PERIOD = Duration.ofMinutes(1);

    private final ClimateSensors sensors;
    private final RuleApplier ruleApplier;
    private final ScheduledExecutorService executor;
    private final ExecutorService worker = Executors.newCachedThreadPool();

    Application(ClimateSensors sensors, RuleApplier ruleApplier, ScheduledExecutorService executor) {
        this.sensors = sensors;
        this.ruleApplier = ruleApplier;
        this.executor = executor;
    }

    public void run() {
        addThreadExecutorWithTimeout("Outdoorsensor", sensors.outdoor(), Duration.ZERO, SENSOR_READ_PERIOD);
        addThreadExecutorWithTimeout("Indoorsensor", sensors.indoor(), Duration.ZERO, SENSOR_READ_PERIOD);
        addThreadExecutorWithTimeout("RuleApplier", ruleApplier, Duration.ZERO, RULE_APPLIER_PERIOD);

        log.info("All setup and running...");
    }

    private void addThreadExecutorWithTimeout(String taskName, Runnable command, Duration initialDelay, Duration period) {
        executor.scheduleAtFixedRate(() -> runTask(taskName, command), initialDelay.toSeconds(), period.toSeconds(), TimeUnit.SECONDS);
    }

    private void runTask(String taskName, Runnable command) {
        try {
            Future<?> future = worker.submit(command);
            try {
                future.get(120, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                log.error("Task {} timed out after 120s, cancelling. Will try again on next schedule.", taskName, te);
                future.cancel(true);
            } catch (ExecutionException ee) {
                log.error("Task {} threw an exception. {}", taskName, ee.getCause() != null ? ee.getCause() : ee);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("Scheduler {} interrupted", taskName, ie);
            }
        } catch (Throwable t) {
            log.error("Unhandled throwable in runTask from {} — scheduler will continue", taskName, t);
        }
    }
}