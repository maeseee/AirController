package org.air_controller.gpio.dingtian_relay;

import org.air_controller.http.HttpsGetRequest;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class DingtianRelay {
    private static final String RELAY_URL = "http://192.168.50.22";
    private static final String GET_RELAY_STATE_URL = RELAY_URL + "/relay_cgi_load.cgi";

    private final HttpsGetRequest httpsGetRequest;
    private final DingtianResponseInterpreter interpreter;

    public DingtianRelay() {
        this(new HttpsGetRequest(), new DingtianResponseInterpreter());
    }

    DingtianRelay(HttpsGetRequest httpsGetRequest, DingtianResponseInterpreter interpreter) {
        this.httpsGetRequest = httpsGetRequest;
        this.interpreter = interpreter;
    }

    public List<Boolean> readStates() {
        final Optional<String> response = httpsGetRequest.sendRequest(GET_RELAY_STATE_URL);
        if (response.isEmpty()) {
            return emptyList();
        }
        return interpreter.interpretRelayState(response.get());
    }
}
