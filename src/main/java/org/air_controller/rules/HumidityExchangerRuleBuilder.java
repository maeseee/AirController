package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentSensors;

import java.util.List;

public class HumidityExchangerRuleBuilder {

    public List<Rule> getHumidityExchangeRules(CurrentSensors sensors) {
        final HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(sensors.indoorData(), sensors.outdoorData());
        return List.of(humidityControlExchanger);
    }
}
