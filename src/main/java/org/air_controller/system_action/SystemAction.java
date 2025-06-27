package org.air_controller.system_action;

import org.air_controller.system.OutputState;

import java.time.ZonedDateTime;

public record SystemAction(ZonedDateTime actionTime, OutputState outputState) {
}
