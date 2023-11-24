package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.OutdoorSensor;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.airController.sensorAdapter.SensorValue;
import org.airController.util.EnvironmentVariable;
import org.airController.util.JsonParser;
import org.airController.util.Logging;
import org.airController.util.SecretsEncryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OutdoorSensorImpl implements OutdoorSensor {
    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String ENVIRONMENT_VARIABLE_API_KEY = "weather_api_key";
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    private final List<OutdoorSensorObserver> observers = new ArrayList<>();
    private final HttpsRequest httpsRequest;

    public OutdoorSensorImpl() throws URISyntaxException {
        this(createHttpRequest(getApiKeyForHttpRequest()));
    }

    OutdoorSensorImpl(HttpsRequest httpsRequest) {
        this.httpsRequest = httpsRequest;
    }

    @Override
    public void run() {
        final Optional<String> request = httpsRequest.sendRequest();
        if (request.isEmpty()) {
            return;
        }

        final AirValue airValues = JsonParser.parse(request.get());
        notifyObservers(new SensorValueImpl(airValues));
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

    private static HttpsRequest createHttpRequest(String decryptedApiKey) throws URISyntaxException {
        final String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + decryptedApiKey;
        final URI uri = new URI(urlString);
        return new HttpsRequest(uri);
    }

    private void notifyObservers(SensorValue outdoorsensorValue) {
        Logging.getLogger().info("New outdoor sensor value: " + outdoorsensorValue);
        observers.forEach(observer -> observer.updateOutdoorSensorValue(outdoorsensorValue));
    }
}
