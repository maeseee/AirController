package org.airController.controllers;

import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.time.LocalDateTime;
import java.util.Optional;

public class InvalidSensorValue implements SensorValue {

    @Override
    public Temperature getTemperature() {
        return null;
    }

    @Override
    public Humidity getHumidity() {
        return null;
    }

    @Override
    public Optional<CarbonDioxide> getCo2() {
        return Optional.empty();
    }

    @Override
    public double getAbsoluteHumidity() {
        return 0;
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return null;
    }

    @Override
    public boolean isSensorValid() {
        return false;
    }
}
