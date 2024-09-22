package org.airController.sensor.qingPing;

import org.airController.controllers.SensorData;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.InvaildArgumentException;
import org.airController.entities.Temperature;
import org.airController.secrets.Secret;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;


public class QingPingSensor implements IndoorSensor {
    static final String APP_KEY = "me8h7AKSR";
    static final String ENVIRONMENT_VARIABLE_APP_SECRET = "qingping_app_secret";
    static final String ENCRYPTED_APP_SECRET = "P2Yg64Btliolc1DDvQFQKYZAb2ufYF10khTLrGfrb9d2kM1tA8ciYhZ2bbQeHdOLlIGmSfM4JQcG6EcnYtvm8w==";
    static final String MAC_PRESSURE_DEVICE = "582D3480A7F4";
    static final String MAC_CO2_DEVICE = "582D34831850";

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final List<IndoorSensorObserver> observers = new ArrayList<>();
    private final QingPingAccessTokenRequest accessTokenRequest;
    private final QingPingListDevicesRequest listDevicesRequest;
    private final QingPingJsonParser parser;
    private final List<String> deviceMacAddresses;

    private String accessToken;
    private LocalDateTime accessTokenValidUntil;

    public QingPingSensor() throws URISyntaxException {
        this(createAccessTokenRequest(getCredentialsForPostRequest()), createListDevicesRequest(), new QingPingJsonParser(),
                Arrays.asList(MAC_PRESSURE_DEVICE, MAC_CO2_DEVICE));
    }

    QingPingSensor(QingPingAccessTokenRequest accessTokenRequest, QingPingListDevicesRequest listDevicesRequest, QingPingJsonParser parser,
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

        final List<QingPingSensorData> sensorDataList = new ArrayList<>();
        for (String mac : deviceMacAddresses) {
            parser.parseDeviceListResponse(request.get(), mac).ifPresent(sensorDataList::add);

        }
        final Optional<SensorData> sensorData = getAverageSensorData(sensorDataList);
        sensorData.ifPresentOrElse(
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

    private void notifyObservers(SensorData sensorData) {
        logger.info("New indoor sensor data: {}", sensorData);
        observers.forEach(observer -> observer.updateIndoorSensorData(sensorData));
    }

    private Optional<SensorData> getAverageSensorData(List<QingPingSensorData> sensorDataList) {
        final List<QingPingSensorData> currentSensorDataList = sensorDataList.stream()
                .filter(sensorData -> sensorData.getTimeStamp().isAfter(LocalDateTime.now().minusHours(1)))
                .toList();
        if (currentSensorDataList.isEmpty()) {
            logger.info("No current indoor data at the moment");
            return Optional.empty();
        }
        try {
            final Temperature temperature = getAverageTemperature(currentSensorDataList);
            final Humidity humidity = getAverageHumidity(currentSensorDataList);
            final CarbonDioxide co2 = getAverageCo2(currentSensorDataList);
            final LocalDateTime time = getNewestTimestamp(currentSensorDataList);
            return Optional.of(new QingPingSensorData(temperature, humidity, co2, time));
        } catch (InvaildArgumentException | NoSuchElementException exception) {
            logger.error("Unexpected error in Exception in getAverageSensorData: :", exception);
        }
        return Optional.empty();
    }

    private Temperature getAverageTemperature(List<QingPingSensorData> currentSensorDataList) throws InvaildArgumentException {
        final OptionalDouble averageTemperature = currentSensorDataList.stream()
                .filter(sensorData -> sensorData.getTemperature().isPresent())
                .mapToDouble(value -> value.getTemperature().get().getCelsius())
                .average();
        return averageTemperature.isPresent() ? Temperature.createFromCelsius(averageTemperature.getAsDouble()) : null;
    }

    private Humidity getAverageHumidity(List<QingPingSensorData> currentSensorDataList) throws InvaildArgumentException {
        final OptionalDouble averageHumidity = currentSensorDataList.stream()
                .filter(sensorData -> sensorData.getHumidity().isPresent())
                .mapToDouble(sensorData -> sensorData.getHumidity().get().getAbsoluteHumidity())
                .average();
        return averageHumidity.isPresent() ? Humidity.createFromAbsolute(averageHumidity.getAsDouble()) : null;
    }

    private CarbonDioxide getAverageCo2(List<QingPingSensorData> currentSensorDataList) throws InvaildArgumentException {
        final OptionalDouble averageCo2 = currentSensorDataList.stream()
                .filter(sensorData -> sensorData.getCo2().isPresent())
                .mapToDouble(value -> value.getCo2().get().getPpm())
                .average();
        return averageCo2.isPresent() ? CarbonDioxide.createFromPpm(averageCo2.getAsDouble()) : null;
    }

    private static LocalDateTime getNewestTimestamp(List<QingPingSensorData> currentSensorDataList) {
        return currentSensorDataList.stream()
                .map(QingPingSensorData::getTimeStamp)
                .max(LocalDateTime::compareTo).orElse(LocalDateTime.now());
    }
}
