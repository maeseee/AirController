package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.secrets.Secret;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonList;


public class QingPingSensor implements IndoorSensor {
    static final String APP_KEY = "me8h7AKSR";
    static final String ENVIRONMENT_VARIABLE_APP_SECRET = "qingping_app_secret";
    static final String ENCRYPTED_APP_SECRET = "P2Yg64Btliolc1DDvQFQKYZAb2ufYF10khTLrGfrb9d2kM1tA8ciYhZ2bbQeHdOLlIGmSfM4JQcG6EcnYtvm8w==";

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final List<IndoorSensorObserver> observers = new ArrayList<>();
    private final QingPingAccessTokenRequest accessTokenRequest;
    private final QingPingListDevicesRequest listDevicesRequest;
    private final JsonQingPingParser parser;
    private final List<String> deviceMacAddresses;

    private String accessToken;
    private LocalDateTime accessTokenValidUntil;

    public QingPingSensor() throws URISyntaxException {
        this(createAccessTokenRequest(getCredentialsForPostRequest()), createListDevicesRequest(), new JsonQingPingParser(),
                singletonList("582D3480A7F4"));
    }

    QingPingSensor(QingPingAccessTokenRequest accessTokenRequest, QingPingListDevicesRequest listDevicesRequest, JsonQingPingParser parser,
                   List<String> deviceMacAddresses) {
        this.accessTokenRequest = accessTokenRequest;
        this.listDevicesRequest = listDevicesRequest;
        this.parser = parser;
        this.deviceMacAddresses = deviceMacAddresses;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            logger.error("Exception in QingPing sensor loop:", exception);
        }
    }

    private void doRun() {
        if (accessTokenValidUntil == null || accessTokenValidUntil.isBefore(LocalDateTime.now())) {
            updateAccessToken();
        }

        final Optional<String> request = listDevicesRequest.sendRequest(accessToken);
        if (request.isEmpty()) {
            logger.error("QingPing sensor request failed");
            return;
        }

        final List<AirValue> airValues = new ArrayList<>();
        for (String mac : deviceMacAddresses) {
            parser.parseDeviceListResponse(request.get(), mac).ifPresent(airValues::add);

        }
        final Optional<AirValue> airValue = getAverageAirValue(airValues);
        airValue.ifPresentOrElse(
                this::notifyObservers,
                () -> logger.error("QingPing sensor out of order"));
    }

    private void updateAccessToken() {
        final Optional<String> request = accessTokenRequest.sendRequest();
        if (request.isEmpty()) {
            logger.error("Access token could not be updated");
            return;
        }

        final Optional<QingPingAccessToken> accessTokenOptional = parser.parseAccessTokenResponse(request.get());
        if (accessTokenOptional.isPresent()) {
            final QingPingAccessToken qingPingAccessToken = accessTokenOptional.get();
            accessToken = qingPingAccessToken.accessToken();
            accessTokenValidUntil = LocalDateTime.now().plusSeconds(qingPingAccessToken.expiresIn() - 60);
        }
    }

    @Override
    public void addObserver(IndoorSensorObserver observer) {
        observers.add(observer);
    }

    private static String getCredentialsForPostRequest() {
        final String appSecret = Secret.getSecret(ENVIRONMENT_VARIABLE_APP_SECRET, ENCRYPTED_APP_SECRET);
        final String credentials = APP_KEY + ":" + appSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static QingPingAccessTokenRequest createAccessTokenRequest(String credentials) throws URISyntaxException {
        final String urlString = "https://oauth.cleargrass.com/oauth2/token";
        final URI uri = new URI(urlString);
        return new QingPingAccessTokenRequest(uri, credentials);
    }

    private static QingPingListDevicesRequest createListDevicesRequest() throws URISyntaxException {
        final String urlString = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(urlString);
        return new QingPingListDevicesRequest(uri);
    }

    private void notifyObservers(AirValue indoorAirValue) {
        logger.info("New indoor sensor value: " + indoorAirValue);
        observers.forEach(observer -> observer.updateIndoorAirValue(indoorAirValue));
    }

    private Optional<AirValue> getAverageAirValue(List<AirValue> airValues) {
        final List<AirValue> currentAirValues = airValues.stream()
                .filter(sensorAirValue -> sensorAirValue.getTime().isAfter(LocalDateTime.now().minusHours(1)))
                .toList();
        try {
            final double averageTemperature = currentAirValues.stream()
                    .mapToDouble(value -> value.getTemperature().getCelsius())
                    .average()
                    .orElseThrow();
            final Temperature temperature = Temperature.createFromCelsius(averageTemperature);
            final double averageHumidity = currentAirValues.stream()
                    .mapToDouble(value -> value.getHumidity().getRelativeHumidity())
                    .average()
                    .orElseThrow();
            final Humidity humidity = Humidity.createFromRelative(averageHumidity);
            final OptionalDouble averageCo2 = currentAirValues.stream()
                    .filter(airValue -> airValue.getCo2().isPresent())
                    .mapToDouble(value -> value.getCo2().get().getPpm())
                    .average();
            final CarbonDioxide co2 = averageCo2.isPresent() ? CarbonDioxide.createFromPpm(averageCo2.getAsDouble()) : null;
            final LocalDateTime time = currentAirValues.stream()
                    .map(AirValue::getTime)
                    .max(LocalDateTime::compareTo).orElse(LocalDateTime.now());
            return Optional.of(new AirValue(temperature, humidity, co2, time));
        } catch (IOException e) {
            // Intentionally left empty
        }
        return Optional.empty();
    }
}
