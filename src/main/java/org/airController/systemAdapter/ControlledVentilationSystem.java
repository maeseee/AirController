package org.airController.systemAdapter;

public interface ControlledVentilationSystem {

    boolean setAirFlowOn(boolean on);

    void setHumidityExchangerOn(boolean on);
}
