package org.air_controller.gpio.dingtian_relay;

class UrlCreator {
    private static final String RELAY_URL = "http://192.168.50.22/";

    public String createGetRelayStatesURL() {
        return RELAY_URL + "relay_cgi_load.cgi";
    }

    public String createSetRelayStateURL(int relay, Action action) {
        if (relay > 4 || relay < 0) {
            throw new IllegalArgumentException("We do not support other relay numbers. It must be between 0 and 4");
        }
        return RELAY_URL +
                "relay_cgi.cgi" +
                "?type=" + ActionType.ON_OFF.getIndex() +
                "&relay=" + relay +
                "&on=" + action.getIndex() +
                "&time=0" + // time in ms for jogging, delay and flash
                "&pwd=0" + // relay password
                "&";
    }
}
