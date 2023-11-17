package org.airController.controllers;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.SensorValue;

class HumidityControlRule {

    private static final AirVO GOAL_AIR_VALUES = new AirVO(23.0, 50.0);

    public boolean turnHumidityExchangerOn(SensorValue indoorSensorValue, SensorValue outdoorSensorValue) {
        if (indoorSensorValue.getValue().isEmpty() || outdoorSensorValue.getValue().isEmpty()) {
            return false;
        }

        final double indoorAbsoluteHumidity = indoorSensorValue.getValue().get().getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorSensorValue.getValue().get().getAbsoluteHumidity();
        final double goalAbsoluteHumidity = GOAL_AIR_VALUES.getAbsoluteHumidity();

        final double diffIndoorToGoal = indoorAbsoluteHumidity - goalAbsoluteHumidity;
        final double diffOutdoorToGoal = outdoorAbsoluteHumidity - goalAbsoluteHumidity;

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
