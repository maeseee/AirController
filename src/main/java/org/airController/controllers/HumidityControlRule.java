package org.airController.controllers;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.IndoorAirValues;
import org.airController.sensorAdapter.OutdoorAirValues;

class HumidityControlRule {

    private static final AirVO GOAL_AIR_VALUES = new AirVO(23.0, 50.0);

    public boolean turnHumidityExchangerOn(IndoorAirValues indoorAirValues, OutdoorAirValues outdoorAirValues) {
        if (indoorAirValues.getAirValues().isEmpty() || outdoorAirValues.getAirValues().isEmpty()) {
            return false;
        }

        final double indoorAbsoluteHumidity = indoorAirValues.getAirValues().get().getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValues.getAirValues().get().getAbsoluteHumidity();
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
