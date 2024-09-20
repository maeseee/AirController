package org.airController.controllers;

import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;

import java.util.Optional;

public class CurrentSensorValues implements IndoorSensorObserver, OutdoorSensorObserver {

    private SensorValue indoorSensorValue;
    private CurrentSensorData outdoorSensorData = new CurrentSensorData();

    public CurrentSensorValues() {
        this(new InvalidSensorValue());
    }

    CurrentSensorValues(SensorValue indoorAirValue) {
        this.indoorSensorValue = indoorAirValue;
    }

    public boolean isIndoorHumidityAboveOutdoorHumidity() {
        Optional<Double> outdoorAbsoluteHumidity = outdoorSensorData.getAbsoluteHumidity();
        if (outdoorAbsoluteHumidity.isEmpty()) {
            return false;
        }
        return indoorSensorValue.getAbsoluteHumidity() > outdoorAbsoluteHumidity.get();
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
    public void updateOutdoorSensorValue(SensorData outdoorSensorData) {
        outdoorSensorData.getTemperature().ifPresent(temperature -> this.outdoorSensorData.setTemperature(temperature));
        outdoorSensorData.getHumidity().ifPresent(humidity -> this.outdoorSensorData.setHumidity(humidity));
        outdoorSensorData.getCo2().ifPresent(co2 -> this.outdoorSensorData.setCo2(co2));
        this.outdoorSensorData.updateTimestamp();
    }
}
