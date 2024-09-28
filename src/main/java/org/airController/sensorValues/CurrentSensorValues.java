package org.airController.sensorValues;

import org.airController.sensor.IndoorSensorObserver;
import org.airController.sensor.OutdoorSensorObserver;

import java.util.Optional;
import java.util.OptionalDouble;

public class CurrentSensorValues implements IndoorSensorObserver, OutdoorSensorObserver {

    private final CurrentSensorData indoorSensorData = new CurrentSensorData();
    private final CurrentSensorData outdoorSensorData = new CurrentSensorData();

    public boolean isIndoorHumidityAboveOutdoorHumidity() {
        final OptionalDouble outdoorAbsoluteHumidity = outdoorSensorData.getAbsoluteHumidity();
        final OptionalDouble indoorAbsoluteHumidity = indoorSensorData.getAbsoluteHumidity();
        if (outdoorAbsoluteHumidity.isEmpty() || indoorAbsoluteHumidity.isEmpty()) {
            return false;
        }
        return indoorAbsoluteHumidity.getAsDouble() > outdoorAbsoluteHumidity.getAsDouble();
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
