package org.air_controller.rules;

import lombok.Getter;
import org.air_controller.system.OutputState;

import java.time.Duration;
import java.time.ZonedDateTime;

@Getter
public class ManualOverrideEvent {
    private final ZonedDateTime timestamp;
    private final OutputState action;
    private final Duration duration;

    public ManualOverrideEvent(ZonedDateTime timestamp, OutputState action, Duration duration) {
        this.timestamp = timestamp;
        this.action = action;
        this.duration = duration;
    }
}
