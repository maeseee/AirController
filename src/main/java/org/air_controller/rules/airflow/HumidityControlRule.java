package org.air_controller.rules.airflow;

import org.air_controller.rules.Confidence;
import org.air_controller.sensor_values.ClimateSensors;
import org.springframework.stereotype.Component;

@Component
class HumidityControlRule implements HumidityExchangeRule {

    private final HumidityControlAirFlow humidityControlAirFlow;

    public HumidityControlRule(ClimateSensors sensors) {
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
