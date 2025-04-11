package org.air_controller.gpio.dingtian_relay;

import java.util.Optional;

public class DingtianResponseInterpreter {
    private static final String OK_RESULT = "0";
    private static final String OFF_VALUE = "0";
    private static final int NUMBER_OF_RELAYS = 4;
    public static final int PROTOCOL_OVERHEAD = 3;

    public Optional<DingtianRelayState> interpretRelayState(String response) {
        final String[] split = response.split("&");
        if (!isResponseValid(split)) {
            return Optional.empty();
        }
        final boolean isOnRelay1 = onStringToBoolean(split[3]);
        final boolean isOnRelay2 = onStringToBoolean(split[4]);
        final boolean isOnRelay3 = onStringToBoolean(split[5]);
        final boolean isOnRelay4 = onStringToBoolean(split[6]);
        final DingtianRelayState relayState = new DingtianRelayState(isOnRelay1, isOnRelay2, isOnRelay3, isOnRelay4);
        return Optional.of(relayState);
    }

    private boolean isResponseValid(String[] split) {
        return split.length == NUMBER_OF_RELAYS + PROTOCOL_OVERHEAD &&
                split[1].equals(OK_RESULT) &&
                split[2].equals(String.valueOf(NUMBER_OF_RELAYS));
    }

    private boolean onStringToBoolean(String isOn) {
        return !OFF_VALUE.equals(isOn);
    }
}
