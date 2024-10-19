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
    public void setAirFlowOn(OutputState state) {
        airFlow.setGpioState(state.isOn());
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
        humidityExchanger.setGpioState(state.isOn());
    }
}
