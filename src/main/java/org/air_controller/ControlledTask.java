package org.air_controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class ControlledTask {
    private static final Duration TIMEOUT = Duration.ofSeconds(50);
    private final ThreadPoolTaskScheduler taskScheduler;

    public ControlledTask(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void execute(String taskName, Runnable task) {
        final Future<?> future = taskScheduler.submit(task);
        try {
            future.get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Task {} timed out and will get stopped", taskName);
            future.cancel(true);
        } catch (InterruptedException e) {
            log.error("Task {} interrupted", taskName);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error("Task {} failed with the exception", taskName, e);
        }
    }
}
