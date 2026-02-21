package org.air_controller.rules;

import org.air_controller.sensor_values.ClimateSensors;

class HumidityControlExchanger implements Rule {

    private final HumidityControlAirFlow humidityControlAirFlow;

    public HumidityControlExchanger(ClimateSensors sensors) {
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
