package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.secrets.Secret;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


public class QingPingSensor implements IndoorSensor {
    static final String APP_KEY = "me8h7AKSR";
    static final String ENVIRONMENT_VARIABLE_APP_SECRET = "qingping_app_secret";
    static final String ENCRYPTED_APP_SECRET = "P2Yg64Btliolc1DDvQFQKYZAb2ufYF10khTLrGfrb9d2kM1tA8ciYhZ2bbQeHdOLlIGmSfM4JQcG6EcnYtvm8w==";

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final List<IndoorSensorObserver> observers = new ArrayList<>();
    private final QingPingAccessTokenRequest accessTokenRequest;
    private final QingPingListDevicesRequest listDevicesRequest;

    private String accessToken;
    private LocalDateTime accessTokenValidUntil;

    public QingPingSensor() throws URISyntaxException {
        this(createAccessTokenRequest(getCredentialsForPostRequest()), createListDevicesRequest());
    }

    QingPingSensor(QingPingAccessTokenRequest accessTokenRequest, QingPingListDevicesRequest listDevicesRequest) {
        this.accessTokenRequest = accessTokenRequest;
        this.listDevicesRequest = listDevicesRequest;
    }

    @Override
    public void run() {
        if (accessTokenValidUntil == null || accessTokenValidUntil.isBefore(LocalDateTime.now())) {
            updateAccessToken();
        }

        final Optional<String> request = listDevicesRequest.sendRequest(accessToken);
        if (request.isEmpty()) {
            return;
        }

        final Optional<AirValue> airValue = JsonQingPingParser.parseDeviceListResponse(request.get());
        airValue.ifPresentOrElse(
                this::notifyObservers,
                () -> logger.error("Outdoor sensor out of order"));
    }

    private void updateAccessToken() {
        final Optional<String> request = accessTokenRequest.sendRequest();
        if (request.isEmpty()) {
            logger.error("Access token could not be updated");
            return;
        }

        final Optional<QingPingAccessToken> accessTokenOptional = JsonQingPingParser.parseAccessTokenResponse(request.get());
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
}
