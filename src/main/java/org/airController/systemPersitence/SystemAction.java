package org.airController.systemPersitence;

import org.airController.system.OutputState;

import java.time.ZonedDateTime;

public record SystemAction(ZonedDateTime actionTime, SystemPart systemPart, OutputState outputState) {
}
