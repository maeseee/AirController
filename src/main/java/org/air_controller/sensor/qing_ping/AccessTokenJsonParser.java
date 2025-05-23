package org.air_controller.sensor.qing_ping;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Optional;

class AccessTokenJsonParser {
    public Optional<AccessTokenData> parse(String jsonString) {
        // https://developer.qingping.co/main/oauthApi
        try {
            final JSONTokener tokener = new JSONTokener(jsonString);
            final JSONObject jsonObject = new JSONObject(tokener);
            final String accessToken = jsonObject.getString("access_token");
            final int expiresIn = jsonObject.getInt("expires_in");
            return Optional.of(new AccessTokenData(accessToken, expiresIn));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
