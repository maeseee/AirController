package org.air_controller.rules;

import org.air_controller.system.OutputState;

import java.time.Duration;
import java.time.ZonedDateTime;

public record ManualOverrideEvent(ZonedDateTime timestamp, OutputState action, Duration duration) {
}
