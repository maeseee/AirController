package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.gpio.GpioPinImpl;
import org.airController.sensorAdapter.OutdoorAirMeasurementObserver;
import org.airController.sensorAdapter.OutdoorAirValues;
import org.airController.util.EnvironmentVariable;
import org.airController.util.JsonParser;
import org.airController.util.SecretsEncryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class OutdoorAirMeasurement implements Runnable {
    private static final Logger logger = Logger.getLogger(GpioPinImpl.class.getName());
    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String ENVIRONMENT_VARIABLE_API_KEY = "weather_api_key";
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    private final List<OutdoorAirMeasurementObserver> observers = new ArrayList<>();
    private final HttpsRequest httpsRequest;

    public OutdoorAirMeasurement() throws URISyntaxException {
        this(createHttpRequest(getApiKeyForHttpRequest()));
    }

    OutdoorAirMeasurement(HttpsRequest httpsRequest) {
        this.httpsRequest = httpsRequest;
    }

    @Override
    public void run() {
        final Optional<String> request = httpsRequest.sendRequest();
        if (request.isEmpty()) {
            return;
        }

        final AirVO airValues = JsonParser.parse(request.get());
        notifyObservers(new OutdoorAirValues(airValues));
    }

    public void addObserver(OutdoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private static String getApiKeyForHttpRequest() {
        final Optional<String> apiKeyOptional = EnvironmentVariable.readEnvironmentVariable(ENVIRONMENT_VARIABLE_API_KEY);
        return apiKeyOptional.orElseGet(OutdoorAirMeasurement::getApiKeyForHttpRequestFromMasterPassword);
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

    private void notifyObservers(OutdoorAirValues outdoorAirValues) {
        logger.info("New outdoor measurement: " + outdoorAirValues);
        observers.forEach(observer -> observer.updateAirMeasurement(outdoorAirValues));
    }
}
