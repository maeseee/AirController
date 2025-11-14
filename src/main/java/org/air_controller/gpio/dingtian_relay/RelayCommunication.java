package org.air_controller.gpio.dingtian_relay;

import org.air_controller.http.HttpsGetRequest;

import java.util.List;

import static java.util.Collections.emptyList;

class RelayCommunication {
    private final UrlCreator urlCreator = new UrlCreator();
    private final HttpsGetRequest httpsGetRequest;
    private final ResponseInterpreter interpreter;

    public RelayCommunication() {
        this(new HttpsGetRequest(), new ResponseInterpreter());
    }

    RelayCommunication(HttpsGetRequest httpsGetRequest, ResponseInterpreter interpreter) {
        this.httpsGetRequest = httpsGetRequest;
        this.interpreter = interpreter;
    }

    public List<Boolean> readStates() {
        final String response = httpsGetRequest.sendRequest(urlCreator.createGetRelayStatesURL());
        if (response.isEmpty()) {
            return emptyList();
        }
        return interpreter.interpretRelayState(response);
    }

    public void setRelayState(int relay, boolean setOn) {
        final String url = urlCreator.createSetRelayStateURL(relay, Action.from(setOn));
        httpsGetRequest.sendRequest(url);
    }
}
