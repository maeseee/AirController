package org.airController.rules;

import org.airController.controllers.Rule;

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
    public Percentage turnOn() {
        return new Percentage(humidityControlAirFlow.turnOn().getPercentage() * -1);
    }
}
