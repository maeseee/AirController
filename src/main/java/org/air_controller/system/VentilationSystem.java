package org.air_controller.system;

public interface VentilationSystem {

    void setAirFlowOn(OutputState state);

    void setHumidityExchangerOn(OutputState state);

    OutputState isAirFlowOn();
}
