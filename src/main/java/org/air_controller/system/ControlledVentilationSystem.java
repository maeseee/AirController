package org.air_controller.system;

import lombok.RequiredArgsConstructor;
import org.air_controller.gpio.GpioPins;

@RequiredArgsConstructor
public class ControlledVentilationSystem implements VentilationSystem {

    private final GpioPins gpioPins;

    @Override
    public void setAirFlowOn(OutputState state) {
        gpioPins.airFlow().setGpioState(state.isOn());
    }

    @Override
    public void setHumidityExchangerOn(OutputState state) {
        gpioPins.humidity().setGpioState(state.isOn());
    }

    @Override
    public OutputState isAirFlowOn() {
        return OutputState.fromIsOnState(gpioPins.airFlow().getGpioState());
    }
}
