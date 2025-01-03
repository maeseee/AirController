package org.air_controller.sensor_values;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface SensorData {
    Optional<Temperature> getTemperature();

    Optional<Humidity> getHumidity();

    Optional<CarbonDioxide> getCo2();

    ZonedDateTime getTimeStamp();
}
