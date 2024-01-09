package org.airController.systemAdapter;

public interface ControlledVentilationSystem {

    boolean isAirFlowOn();
    boolean setAirFlowOn(boolean on);

    void setHumidityExchangerOn(boolean on);
}
