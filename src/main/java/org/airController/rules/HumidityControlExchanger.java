package org.airController.rules;

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
    public Confident turnOnConfident() {
        double impact = humidityControlAirFlow.turnOnConfident().getWeightedConfidentValue() < 0 ? 1 : -1;
        return new Confident(impact);
    }
}
