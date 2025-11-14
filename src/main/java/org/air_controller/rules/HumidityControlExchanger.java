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
        double impact = humidityControlAirFlow.turnOnConfidence().getWeightedConfidenceValue() < 0 ? 1 : -1;
        return new Confidence(impact);
    }
}
