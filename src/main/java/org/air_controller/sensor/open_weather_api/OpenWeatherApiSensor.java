package org.air_controller.sensor.open_weather_api;

import org.air_controller.http.HttpsGetRequest;
import org.air_controller.secrets.Secret;
import org.air_controller.sensor.SensorReader;


public class OpenWeatherApiSensor implements SensorReader {
    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String ENVIRONMENT_VARIABLE_API_KEY = "weather_api_key";
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    private final HttpsGetRequest httpsGetRequest;

    public OpenWeatherApiSensor() {
        this(new HttpsGetRequest());
    }

    OpenWeatherApiSensor(HttpsGetRequest httpsGetRequest) {
        this.httpsGetRequest = httpsGetRequest;
    }

    public String readData() {
        return httpsGetRequest.sendRequest(createHttpsGetUrl());

    }

    private String createHttpsGetUrl() {
        final String apiKeyForHttpRequest = getDecryptedApiKeyForHttpRequest();
        return "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + apiKeyForHttpRequest;
    }

    private static String getDecryptedApiKeyForHttpRequest() {
        return Secret.getSecret(ENVIRONMENT_VARIABLE_API_KEY, ENCRYPTED_API_KEY);
    }
}
