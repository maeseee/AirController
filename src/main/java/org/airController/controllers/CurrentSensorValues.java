package org.airController.controllers;

import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;

import java.util.Optional;

public class CurrentSensorValues implements IndoorSensorObserver, OutdoorSensorObserver {

    private final CurrentSensorData indoorSensorData = new CurrentSensorData();
    private final CurrentSensorData outdoorSensorData = new CurrentSensorData();

    public boolean isIndoorHumidityAboveOutdoorHumidity() {
        final Optional<Double> outdoorAbsoluteHumidity = outdoorSensorData.getAbsoluteHumidity();
        final Optional<Double> indoorAbsoluteHumidity = indoorSensorData.getAbsoluteHumidity();
        if (outdoorAbsoluteHumidity.isEmpty() || indoorAbsoluteHumidity.isEmpty()) {
            return false;
        }
        return indoorAbsoluteHumidity.get() > outdoorAbsoluteHumidity.get();
    }

    public Optional<Humidity> getIndoorHumidity() {
        return indoorSensorData.getHumidity();
    }

    public Optional<Temperature> getIndoorTemperature() {
        return indoorSensorData.getTemperature();
    }

    public Optional<CarbonDioxide> getIndoorCo2() {
        return indoorSensorData.getCo2();
    }

    @Override
    public void updateIndoorSensorData(SensorData indoorSensorData) {
        indoorSensorData.getTemperature().ifPresent(this.indoorSensorData::setTemperature);
        indoorSensorData.getHumidity().ifPresent(this.indoorSensorData::setHumidity);
        indoorSensorData.getCo2().ifPresent(this.indoorSensorData::setCo2);
        this.indoorSensorData.updateTimestamp();
    }

    @Override
    public void updateOutdoorSensorData(SensorData sensorData) {
        sensorData.getTemperature().ifPresent(this.outdoorSensorData::setTemperature);
        sensorData.getHumidity().ifPresent(this.outdoorSensorData::setHumidity);
        sensorData.getCo2().ifPresent(this.outdoorSensorData::setCo2);
        this.outdoorSensorData.updateTimestamp();
    }
}
