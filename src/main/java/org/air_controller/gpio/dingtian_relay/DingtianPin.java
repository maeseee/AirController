package org.air_controller.gpio.dingtian_relay;

import org.air_controller.gpio.GpioPin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DingtianPin implements GpioPin {
    private static final Logger logger = LogManager.getLogger(DingtianPin.class);

    private final String name;
    private final DingtianRelay relay;
    private final RelayCommunication communication;

    public DingtianPin(DingtianRelay relay, boolean initialHigh) {
        this(relay, initialHigh, new RelayCommunication());
    }

    DingtianPin(DingtianRelay relay, boolean initialHigh, RelayCommunication communication) {
        this.name = relay.name();
        this.relay = relay;
        this.communication = communication;
        logger.info("{} set initial to {}", name, initialHigh ? "on" : "off");
        setGpioState(initialHigh);
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            logger.info("{} set to {}", name, stateOn ? "on" : "off");
            communication.setRelayState(relay.getRelayIndex(), stateOn);
        }
    }

    @Override
    public boolean getGpioState() {
        final List<Boolean> states = communication.readStates();
        if (relay.getRelayIndex() < states.size()) {
            return states.get(relay.getRelayIndex());
        }
        logger.error("Could not read the gpio state of {}", name);
        return false;
    }
}
