package org.airController.controllers;

class AirQualityChecker {

    private static final double UPPER_CO2_LIMIT = 850.0;

    public boolean turnFreshAirOn(SensorValues sensorValues) {
        if (sensorValues.getIndoorCo2().isEmpty()) {
            return false;
        }
        return sensorValues.getIndoorCo2().get().getPpm() > UPPER_CO2_LIMIT;
    }
}
