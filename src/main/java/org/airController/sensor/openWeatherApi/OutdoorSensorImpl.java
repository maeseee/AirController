package org.airController.sensor.openWeatherApi;

import org.airController.secrets.Secret;
import org.airController.sensorAdapter.OutdoorSensor;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OutdoorSensorImpl implements OutdoorSensor {
    private static final Logger logger = LogManager.getLogger(OutdoorSensorImpl.class);
    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String ENVIRONMENT_VARIABLE_API_KEY = "weather_api_key";
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    private final List<OutdoorSensorObserver> observers = new ArrayList<>();
    private final HttpsGetRequest httpsGetRequest;

    public OutdoorSensorImpl() throws URISyntaxException {
        this(createHttpsGetRequest(getApiKeyForHttpRequest()));
    }

    OutdoorSensorImpl(HttpsGetRequest httpsGetRequest) {
        this.httpsGetRequest = httpsGetRequest;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            logger.error("Exception in OutdoorSensor loop:", exception);
        }
    }

    private void doRun() {
        final Optional<String> request = httpsGetRequest.sendRequest();
        if (request.isEmpty()) {
            logger.error("Outdoor sensor request failed");
            return;
        }

        final Optional<OpenWeatherApiSensorData> airValue = JsonOpenWeatherApiParser.parse(request.get());
        airValue.ifPresentOrElse(
                this::notifyObservers,
                () -> logger.error("Outdoor sensor out of order"));
    }

    @Override
    public void addObserver(OutdoorSensorObserver observer) {
        observers.add(observer);
    }

    private static String getApiKeyForHttpRequest() {
        return Secret.getSecret(ENVIRONMENT_VARIABLE_API_KEY, ENCRYPTED_API_KEY);
    }

    private static HttpsGetRequest createHttpsGetRequest(String decryptedApiKey) throws URISyntaxException {
        final String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + decryptedApiKey;
        final URI uri = new URI(urlString);
        return new HttpsGetRequest(uri);
    }

    private void notifyObservers(OpenWeatherApiSensorData outdoorSensorData) {
        logger.info("New outdoor sensor data: {}", outdoorSensorData);
        observers.forEach(observer -> observer.updateOutdoorSensorValue(outdoorSensorData));
    }
}
