package org.airController.systemPersitence;

import org.airController.system.OutputState;

import java.time.LocalDateTime;

public record SystemAction(LocalDateTime actionTime, SystemPart systemPart, OutputState outputState) {
}
