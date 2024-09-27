package org.airController.system;

import org.airController.gpio.GpioPin;

public class ControlledVentilationSystem implements VentilationSystem {

    private final GpioPin airFlow;
    private final GpioPin humidityExchanger;

    public ControlledVentilationSystem(GpioPin airFlow, GpioPin humidityExchanger) {
        this.airFlow = airFlow;
        this.humidityExchanger = humidityExchanger;
    }

    @Override
    public void setAirFlowOn(boolean on) {
        airFlow.setGpioState(on);
    }

    @Override
    public void setHumidityExchangerOn(boolean on) {
        humidityExchanger.setGpioState(on);
    }
}
