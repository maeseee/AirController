package org.air_controller.gpio.dingtian_relay;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.gpio.GpioPin;

import java.util.List;

@Slf4j
public class DingtianPin implements GpioPin {
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
        log.info("{} set initial to {}", name, initialHigh ? "on" : "off");
        setGpioState(initialHigh);
    }

    @Override
    public void setGpioState(boolean stateOn) {
        if (getGpioState() != stateOn) {
            communication.setRelayState(relay.getRelayIndex(), stateOn);
        }
    }

    @Override
    public boolean getGpioState() {
        final List<Boolean> states = communication.readStates();
        if (relay.getRelayIndex() < states.size()) {
            return states.get(relay.getRelayIndex());
        }
        log.error("Could not read the gpio state of {}", name);
        return false;
    }
}
