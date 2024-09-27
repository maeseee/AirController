package org.airController.sensorValues;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SensorData {
    Optional<Temperature> getTemperature();

    Optional<Humidity> getHumidity();

    Optional<CarbonDioxide> getCo2();

    LocalDateTime getTimeStamp();
}
