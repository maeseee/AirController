package org.airController.controllers;

import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;

import java.util.Optional;

public class CurrentSensorValues implements IndoorSensorObserver, OutdoorSensorObserver {

    private SensorValue indoorSensorValue;
    private SensorValue outdoorSensorValue;

    public CurrentSensorValues() {
        this(new InvalidSensorValue(), new InvalidSensorValue());
    }

    CurrentSensorValues(SensorValue indoorAirValue, SensorValue outdoorSensorValue) {
        this.indoorSensorValue = indoorAirValue;
        this.outdoorSensorValue = outdoorSensorValue;
    }

    public boolean isIndoorHumidityAboveOutdoorHumidity() {
        return indoorSensorValue.getAbsoluteHumidity() > outdoorSensorValue.getAbsoluteHumidity();
    }

    public Optional<Humidity> getIndoorHumidity() {
        return indoorSensorValue.isSensorValid() ? Optional.of(indoorSensorValue.getHumidity()) : Optional.empty();
    }

    public Optional<CarbonDioxide> getIndoorCo2() {
        return indoorSensorValue.isSensorValid() ? indoorSensorValue.getCo2() : Optional.empty();
    }

    @Override
    public void updateIndoorSensorValue(SensorValue indoorSensorValue) {
        this.indoorSensorValue = indoorSensorValue;
    }

    @Override
    public void updateOutdoorSensorValue(SensorValue outdoorSensorValue) {
        this.outdoorSensorValue = outdoorSensorValue;
    }
}
