package org.airController.systemAdapter;

public interface ControlledVentilationSystem {

    boolean isAirFlowOn();
    void setAirFlowOn(boolean on);

    boolean isHumidityExchangerOn();
    void setHumidityExchangerOn(boolean on);
}
