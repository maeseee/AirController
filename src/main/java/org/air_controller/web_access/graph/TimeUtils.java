package org.air_controller.web_access.graph;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtils {

    public static LocalDateTime toLocalDateTime(ZonedDateTime timestamp) {
        return timestamp
                .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime();
    }
}
