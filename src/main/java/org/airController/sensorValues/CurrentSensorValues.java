package org.airController.sensorValues;

import org.airController.sensor.IndoorSensorObserver;
import org.airController.sensor.OutdoorSensorObserver;

import java.util.Optional;

public class CurrentSensorValues implements IndoorSensorObserver, OutdoorSensorObserver {

    private final CurrentSensorData indoorSensorData = new CurrentSensorData();
    private final CurrentSensorData outdoorSensorData = new CurrentSensorData();

    public Optional<Humidity> getIndoorHumidity() {
        return indoorSensorData.getHumidity();
    }

    public Optional<Temperature> getIndoorTemperature() {
        return indoorSensorData.getTemperature();
    }

    public Optional<CarbonDioxide> getIndoorCo2() {
        return indoorSensorData.getCo2();
    }

    public Optional<Humidity> getOutdoorHumidity() {
        return outdoorSensorData.getHumidity();
    }

    @Override
    public void updateIndoorSensorData(SensorData indoorSensorData) {
        updateSensorData(indoorSensorData, this.indoorSensorData);
    }

    @Override
    public void updateOutdoorSensorData(SensorData outdoorSensorData) {
        updateSensorData(outdoorSensorData, this.outdoorSensorData);
    }

    private void updateSensorData(SensorData newSensorData, CurrentSensorData targetSensorData) {
        targetSensorData.setTemperature(newSensorData.getTemperature().orElse(null));
        targetSensorData.setHumidity(newSensorData.getHumidity().orElse(null));
        targetSensorData.setCo2(newSensorData.getCo2().orElse(null));
        targetSensorData.setTimestamp(newSensorData.getTimeStamp());
    }
}
