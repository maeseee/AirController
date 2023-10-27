package org.systemAdapter;

public interface ControlledVentilationSystem {

    boolean isAirFlowOn();
    void setAirFlowOn(boolean on);

    boolean isHumidityExchangerOn();
    void setHumidityExchangerOn(boolean on);
}
