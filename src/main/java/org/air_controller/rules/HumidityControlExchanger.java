package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentSensorData;

public class HumidityControlExchanger implements Rule {

    private final HumidityControlAirFlow humidityControlAirFlow;

    public HumidityControlExchanger(CurrentSensorData currentIndoorSensorData, CurrentSensorData currentOutdoorSensorData) {
        humidityControlAirFlow = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);
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
