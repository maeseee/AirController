package org.air_controller.gpio.dingtian_relay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class DingtianResponseInterpreter {
    private static final String OK_RESULT = "0";
    private static final String OFF_VALUE = "0";
    public static final int PROTOCOL_OVERHEAD = 3;

    public List<Boolean> interpretRelayState(String response) {
        final String[] data = response.split("&");
        final int numberOfRelays;
        try {
            validateResultState(data);
            numberOfRelays = readNumberOfRelays(data);
        } catch (IOException e) {
            return emptyList();
        }
        final List<Boolean> relayStates = new ArrayList<>(numberOfRelays);
        for (int relay = 0; relay < numberOfRelays; relay++) {
            relayStates.add(onStringToBoolean(data[relay + PROTOCOL_OVERHEAD]));
        }
        return relayStates;
    }

    private void validateResultState(String[] data) throws IOException {
        if (data.length <= PROTOCOL_OVERHEAD || !data[1].equals(OK_RESULT)) {
            throw new IOException("Result state is invalid");
        }
    }

    private int readNumberOfRelays(String[] data) throws IOException {
        final int numberOfRelays = Integer.parseInt(data[2]);
        if (data.length != PROTOCOL_OVERHEAD + numberOfRelays) {
            throw new IOException("The result has an invalid number of data values");
        }
        return numberOfRelays;
    }

    private boolean onStringToBoolean(String isOn) {
        return !OFF_VALUE.equals(isOn);
    }
}
