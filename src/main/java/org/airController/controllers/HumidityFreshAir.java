package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.io.IOException;

class HumidityFreshAir {

    private static final AirValue TARGET_AIR_VALUE;

    static {
        try {
            TARGET_AIR_VALUE = new AirValue(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(50.0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean turnFreshAirOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();
        final double targetAbsoluteHumidity = TARGET_AIR_VALUE.getAbsoluteHumidity();

        final double diffIndoorToGoal = indoorAbsoluteHumidity - targetAbsoluteHumidity;
        final double diffOutdoorToGoal = outdoorAbsoluteHumidity - targetAbsoluteHumidity;

        return numbersWithDifferentSigns(diffIndoorToGoal, diffOutdoorToGoal);
    }

    private boolean numbersWithDifferentSigns(double diffIndoorToGoal, double diffOutdoorToGoal) {
        return Math.signum(diffIndoorToGoal) * Math.signum(diffOutdoorToGoal) < 0;
    }
}
