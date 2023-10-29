package org.system;

import org.systemAdapter.ControlledVentilationSystem;

public class ControlledVentilationSystemImpl implements ControlledVentilationSystem {

    private boolean airFlowOn = true;
    private boolean humidityExchangerOn = false;

    @Override
    public boolean isAirFlowOn(){
        return airFlowOn;
    }

    @Override
    public void setAirFlowOn(boolean on) {
        airFlowOn = on;
    }

    @Override
    public boolean isHumidityExchangerOn(){
        return humidityExchangerOn;
    }

    @Override
    public void setHumidityExchangerOn(boolean on) {
        humidityExchangerOn = on;
    }
}
