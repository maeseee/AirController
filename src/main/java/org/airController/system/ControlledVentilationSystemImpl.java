package org.airController.system;

import org.airController.gpio.GpioPin;
import org.airController.systemAdapter.ControlledVentilationSystem;

public class ControlledVentilationSystemImpl implements ControlledVentilationSystem {

    private final GpioPin airFlow;
    private final GpioPin humidityExchanger;

    public ControlledVentilationSystemImpl(GpioPin airFlow, GpioPin humidityExchanger) {
        this.airFlow = airFlow;
        this.humidityExchanger = humidityExchanger;

        setAirFlowOn(true);
        setHumidityExchangerOn(false);
    }

    @Override
    public boolean isAirFlowOn() {
        return airFlow.getGpioState();
    }

    @Override
    public void setAirFlowOn(boolean on) {
        airFlow.setGpioState(on);
    }

    @Override
    public boolean isHumidityExchangerOn() {
        return humidityExchanger.getGpioState();
    }

    @Override
    public void setHumidityExchangerOn(boolean on) {
        humidityExchanger.setGpioState(on);
    }
}
