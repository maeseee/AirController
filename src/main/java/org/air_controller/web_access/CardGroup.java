package org.air_controller.web_access;

import java.time.ZonedDateTime;
import java.util.List;

public record CardGroup(ZonedDateTime from, List<CardView> cards) {
}
