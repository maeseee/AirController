package org.air_controller.sensor.open_weather_api_adapter;

import java.time.ZonedDateTime;

public record SolarEvent(ZonedDateTime sunrise, ZonedDateTime sunset) {
}
