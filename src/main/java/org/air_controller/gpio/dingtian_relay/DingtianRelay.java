package org.air_controller.gpio.dingtian_relay;

import org.air_controller.http.HttpsGetRequest;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class DingtianRelay {
    private final DingtianUrlCreator urlCreator = new DingtianUrlCreator();
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
        final Optional<String> response = httpsGetRequest.sendRequest(urlCreator.createGetRelayStatesURL());
        if (response.isEmpty()) {
            return emptyList();
        }
        return interpreter.interpretRelayState(response.get());
    }

    public void setRelayState(int relay, boolean setOn) {
        final String url = urlCreator.createSetRelayStateURL(relay, DingtianAction.from(setOn));
        httpsGetRequest.sendRequest(url);
    }
}
