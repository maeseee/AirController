package org.controllers;

import org.sensors.IndoorAirValues;
import org.sensors.OutdoorAirValues;

public interface HumidityExchangerRule {
    boolean turnHumidityExchangerOn(IndoorAirValues indoorAirValues, OutdoorAirValues outdoorAirValues);
}
