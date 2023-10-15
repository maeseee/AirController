package org.outputsystem;

public class AirControllingSystem {

    private boolean airFlowOn = true;
    private boolean humidityExchangerOn = false;

    public boolean isAirFlowOn(){
        return airFlowOn;
    }

    public void setAirFlowOn(boolean on) {
        airFlowOn = on;
    }

    public boolean isHumidityExchangerOn(){
        return humidityExchangerOn;
    }

    public void setHumidityExchangerOn(boolean on) {
        humidityExchangerOn = on;
    }
}
