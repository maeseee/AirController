package org.airController.system;

public interface VentilationSystem {

    void setAirFlowOn(OutputState state);

    void setHumidityExchangerOn(OutputState state);
}
