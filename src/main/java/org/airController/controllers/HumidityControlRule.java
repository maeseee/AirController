package org.airController.controllers;

import org.airController.entities.AirVO;
import org.airController.entities.Humidity;
import org.airController.sensorAdapter.SensorValue;

import java.io.IOException;

class HumidityControlRule {

    private static final Humidity TARGET_HUMIDITY;

    static {
        try {
            TARGET_HUMIDITY = Humidity.createFromRelative(50.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean turnHumidityExchangerOn(SensorValue indoorSensorValue, SensorValue outdoorSensorValue) {
        if (indoorSensorValue.getValue().isEmpty() || outdoorSensorValue.getValue().isEmpty()) {
            return false;
        }
        final AirVO indoor = indoorSensorValue.getValue().get();
        final AirVO outdoor = outdoorSensorValue.getValue().get();

        final double indoorAbsoluteHumidity = indoor.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoor.getAbsoluteHumidity();
        final double targetAbsoluteHumidity = TARGET_HUMIDITY.getAbsoluteHumidity(indoor.getTemperature());

        final double diffIndoorToGoal = indoorAbsoluteHumidity - targetAbsoluteHumidity;
        final double diffOutdoorToGoal = outdoorAbsoluteHumidity - targetAbsoluteHumidity;

        if (numbersWithDifferentSigns(diffIndoorToGoal, diffOutdoorToGoal)) {
            return false;
        }

        return isIndoorMoreAccurate(diffOutdoorToGoal, diffIndoorToGoal);
    }

    private boolean numbersWithDifferentSigns(double diffIndoorToGoal, double diffOutdoorToGoal) {
        return Math.signum(diffIndoorToGoal) * Math.signum(diffOutdoorToGoal) < 0;
    }

    private boolean isIndoorMoreAccurate(double diffOutdoorToGoal, double diffIndoorToGoal) {
        return Math.abs(diffOutdoorToGoal) > Math.abs(diffIndoorToGoal);
    }
}
