package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;

import java.io.IOException;

class HumidityFreshAirRule {

    private static final Temperature TARGET_TEMPERATURE = Temperature.createFromCelsius(23);
    private static final Humidity TARGET_HUMIDITY;

    static {
        try {
            TARGET_HUMIDITY = Humidity.createFromRelative(50.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean turnFreshAirOn(AirValue indoorAirValue, AirValue outdoorAirValue) {
        final double indoorAbsoluteHumidity = indoorAirValue.getAbsoluteHumidity();
        final double outdoorAbsoluteHumidity = outdoorAirValue.getAbsoluteHumidity();
        final double targetAbsoluteHumidity = TARGET_HUMIDITY.getAbsoluteHumidity(TARGET_TEMPERATURE);

        final double diffIndoorToGoal = indoorAbsoluteHumidity - targetAbsoluteHumidity;
        final double diffOutdoorToGoal = outdoorAbsoluteHumidity - targetAbsoluteHumidity;

        return numbersWithDifferentSigns(diffIndoorToGoal, diffOutdoorToGoal);
    }

    private boolean numbersWithDifferentSigns(double diffIndoorToGoal, double diffOutdoorToGoal) {
        return Math.signum(diffIndoorToGoal) * Math.signum(diffOutdoorToGoal) < 0;
    }
}
