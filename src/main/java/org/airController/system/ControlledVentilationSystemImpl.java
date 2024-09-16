package org.airController.system;

import org.airController.gpioAdapter.GpioPin;
import org.airController.systemAdapter.ControlledVentilationSystem;

public class ControlledVentilationSystemImpl implements ControlledVentilationSystem {

    private final GpioPin airFlow;
    private final GpioPin humidityExchanger;

    public ControlledVentilationSystemImpl(GpioPin airFlow, GpioPin humidityExchanger) {
        this.airFlow = airFlow;
        this.humidityExchanger = humidityExchanger;
    }

    @Override
    public boolean setAirFlowOn(boolean on) {
        return airFlow.setGpioState(on);
    }

    @Override
    public void setHumidityExchangerOn(boolean on) {
        humidityExchanger.setGpioState(on);
    }
}
