package org.air_controller.rules;

import org.air_controller.sensor_values.ClimateSensors;

import java.util.List;

public class HumidityExchangerRuleBuilder {

    public List<Rule> getHumidityExchangeRules(ClimateSensors sensors) {
        final HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(sensors);
        return List.of(humidityControlExchanger);
    }
}
