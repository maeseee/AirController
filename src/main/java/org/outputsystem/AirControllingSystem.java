package org.outputsystem;

public class AirControllingSystem {

    private boolean airFlowOn = true;
    private boolean rotiOn = false;

    public boolean isAirFlowOn(){
        return airFlowOn;
    }

    public void setAirFlowOn(boolean on) {
        airFlowOn = on;
    }

    public boolean isRotiOn(){
        return rotiOn;
    }

    public void setRotiOn(boolean on) {
        rotiOn = on;
    }
}
