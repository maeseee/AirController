package org.air_controller.rules;

import org.air_controller.sensor_values.ClimateSensors;
import org.springframework.stereotype.Component;

@Component
class HumidityExchangeExchanger implements HumidityExchangeRule {

    private final HumidityControlAirFlow humidityControlAirFlow;

    public HumidityExchangeExchanger(ClimateSensors sensors) {
        this.humidityControlAirFlow = new HumidityControlAirFlow(sensors);
    }

    @Override
    public String name() {
        return "Humidity exchanger control";
    }

    @Override
    public Confidence turnOnConfidence() {
        return humidityControlAirFlow.turnOnConfidence().invertConfidence();
    }
}
