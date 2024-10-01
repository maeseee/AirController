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
    public Percentage turnOnConfident() {
        double impact = humidityControlAirFlow.turnOnConfident().getPercentage() < 0 ? 1 : -1;
        return new Percentage(impact);
    }
}
