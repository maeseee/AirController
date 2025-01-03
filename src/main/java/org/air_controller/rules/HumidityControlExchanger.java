package org.air_controller.rules;

public class HumidityControlExchanger implements Rule {

    private final HumidityControlAirFlow humidityControlAirFlow;

    public HumidityControlExchanger(HumidityControlAirFlow humidityControlAirFlow) {

        this.humidityControlAirFlow = humidityControlAirFlow;
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
