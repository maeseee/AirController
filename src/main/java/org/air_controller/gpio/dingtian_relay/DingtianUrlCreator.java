package org.air_controller.gpio.dingtian_relay;

public class DingtianUrlCreator {
    private static final String RELAY_URL = "http://192.168.50.22/";

    public String createGetRelayStatesURL() {
        return RELAY_URL + "relay_cgi_load.cgi";
    }

    public String createSetRelayStateURL(int relay, DingtianAction action) {
        if (relay > 4 || relay < 0) {
            throw new IllegalArgumentException("We do not support other relay numbers. It must be between 0 and 4");
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(RELAY_URL);
        builder.append("relay_cgi.cgi");
        builder.append("?type=").append(DingtianActionType.ON_OFF.getIndex());
        builder.append("&relay=").append(relay);
        builder.append("&on=").append(action.getIndex());
        builder.append("&time=0"); // time in ms for jogging, delay and flash
        builder.append("&pwd=0"); // relay password
        builder.append("&");
        return builder.toString();
    }
}
