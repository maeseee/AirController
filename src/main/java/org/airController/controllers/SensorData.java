package org.airController.controllers;

import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SensorData {
    Optional<Temperature> getTemperature();

    Optional<Humidity> getHumidity();

    Optional<CarbonDioxide> getCo2();

    LocalDateTime getTimeStamp();
}
