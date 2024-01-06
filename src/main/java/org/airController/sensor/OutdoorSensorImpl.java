package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.OutdoorSensor;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.airController.util.EnvironmentVariable;
import org.airController.util.JsonParser;
import org.airController.util.SecretsEncryption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        final Optional<String> request = httpsGetRequest.sendRequest();
        if (request.isEmpty()) {
            return;
        }

        final Optional<AirValue> airValue = JsonParser.parse(request.get());
        airValue.ifPresentOrElse(
                this::notifyObservers,
                () -> logger.error("Outdoor sensor out of order"));
    }

    @Override
    public void addObserver(OutdoorSensorObserver observer) {
        observers.add(observer);
    }

    private static String getApiKeyForHttpRequest() {
        final Optional<String> apiKeyOptional = EnvironmentVariable.readEnvironmentVariable(ENVIRONMENT_VARIABLE_API_KEY);
        return apiKeyOptional.orElseGet(OutdoorSensorImpl::getApiKeyForHttpRequestFromMasterPassword);
    }

    private static String getApiKeyForHttpRequestFromMasterPassword() {
        System.out.println("Enter the master password:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String masterPassword;
        try {
            masterPassword = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
        final String decryptedApiKey = secretsEncryption.decrypt(ENCRYPTED_API_KEY);
        if (decryptedApiKey == null) {
            System.err.println("Wrong master password entered!");
            return null;
        }
        System.out.println("API_KEY is " + decryptedApiKey);
        return decryptedApiKey;
    }

    private static HttpsGetRequest createHttpsGetRequest(String decryptedApiKey) throws URISyntaxException {
        final String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + decryptedApiKey;
        final URI uri = new URI(urlString);
        return new HttpsGetRequest(uri);
    }

    private void notifyObservers(AirValue outdoorAirValue) {
        logger.info("New outdoor sensor value: " + outdoorAirValue);
        observers.forEach(observer -> observer.updateOutdoorAirValue(outdoorAirValue));
    }
}
