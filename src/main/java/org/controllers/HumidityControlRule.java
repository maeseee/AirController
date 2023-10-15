package org.controllers;

import org.entities.AirValues;
import org.entities.Humidity;
import org.entities.Temperature;
import org.sensors.IndoorAirValues;
import org.sensors.OutdoorAirValues;

public class HumidityControlRule implements HumidityExchangerRule {

    private static final AirValues GOAL_AIR_VALUES = new AirValues(new Humidity(50), new Temperature(23));

    @Override
    public boolean turnHumidityExchangerOn(IndoorAirValues indoorAirValues, OutdoorAirValues outdoorAirValues) {
        final double indoorAbsoluteHumidity = indoorAirValues.getAirValues().getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValues.getAirValues().getAbsoluteHumidity();
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
