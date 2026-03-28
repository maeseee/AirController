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
        final TrackableRunnable wrappedTask = new TrackableRunnable(task);
        final Future<?> future = taskScheduler.submit(wrappedTask);
        try {
            future.get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException exception) {
            log.error("Task {} timed out and will get stopped.\n{}", taskName, getCurrentThreadCallStack(wrappedTask), exception);
            future.cancel(true);
        } catch (InterruptedException exception) {
            log.error("Task {} interrupted.\n{}", taskName, getCurrentThreadCallStack(wrappedTask), exception);
            Thread.currentThread().interrupt();
        } catch (ExecutionException exception) {
            log.error("Task {} failed with the exception.\n{}", taskName, getCurrentThreadCallStack(wrappedTask), exception);
        }
    }

    private String getCurrentThreadCallStack(TrackableRunnable wrappedTask) {
        final String currentThreadCallStack = wrappedTask.getCurrentThreadCallStack();
        return "Task Callback is:\n" + currentThreadCallStack;
    }
}
