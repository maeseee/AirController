package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;

public class SensorValues implements IndoorSensorObserver, OutdoorSensorObserver {

    private AirValue indoorAirValue;
    private AirValue outdoorAirValue;

    public SensorValues() {
    }

    SensorValues(AirValue indoorAirValue, AirValue outdoorAirValue) {
        this.indoorAirValue = indoorAirValue;
        this.outdoorAirValue = outdoorAirValue;
    }

    public double getIndoorAbsoluteHumidity() {
        return indoorAirValue.getAbsoluteHumidity();
    }

    public double getOutdoorAbsoluteHumidity() {
        return outdoorAirValue.getAbsoluteHumidity();
    }

    public Temperature getIndoorTemperature() {
        return indoorAirValue.getTemperature();
    }

    public Temperature getOutdoorTemperature() {
        return outdoorAirValue.getTemperature();
    }

    public boolean isASensorValueMissing() {
        return indoorAirValue == null || outdoorAirValue == null;
    }

    @Override
    public void updateIndoorAirValue(AirValue indoorAirValue) {
        this.indoorAirValue = indoorAirValue;
    }

    @Override
    public void updateOutdoorAirValue(AirValue outdoorAirValue) {
        this.outdoorAirValue = outdoorAirValue;
    }
}
