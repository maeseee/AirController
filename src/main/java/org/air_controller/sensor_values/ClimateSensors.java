package org.air_controller.sensor_values;

import org.air_controller.sensor.ClimateSensor;

public record ClimateSensors(ClimateSensor indoor, ClimateSensor outdoor) {
}
