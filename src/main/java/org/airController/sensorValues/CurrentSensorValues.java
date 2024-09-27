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
