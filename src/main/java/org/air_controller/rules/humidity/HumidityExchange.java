package org.air_controller.rules.humidity;

import org.air_controller.rules.Confidence;
import org.air_controller.rules.airflow.HumidityControl;
import org.air_controller.sensor_values.ClimateSensors;
import org.springframework.stereotype.Component;

@Component
class HumidityExchange implements HumidityExchangeRule {

    private final HumidityControl humidityControl;

    public HumidityExchange(ClimateSensors sensors) {
        this.humidityControl = new HumidityControl(sensors);
    }

    @Override
    public String name() {
        return "Humidity exchanger control";
    }

    @Override
    public Confidence turnOnConfidence() {
        return humidityControl.turnOnConfidence().invertConfidence();
    }
}
