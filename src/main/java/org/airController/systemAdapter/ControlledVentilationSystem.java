package org.airController.systemAdapter;

public interface ControlledVentilationSystem {

    boolean isAirFlowOn();
    boolean setAirFlowOn(boolean on);

    boolean isHumidityExchangerOn();
    void setHumidityExchangerOn(boolean on);
}
