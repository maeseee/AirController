package org.airController.sensor.qingPing;

import org.airController.controllers.SensorData;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.InvaildArgumentException;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;


public class QingPingSensor implements IndoorSensor {
    static final String MAC_PRESSURE_DEVICE = "582D3480A7F4";
    static final String MAC_CO2_DEVICE = "582D34831850";

    private static final Logger logger = LogManager.getLogger(QingPingSensor.class);

    private final List<IndoorSensorObserver> observers = new ArrayList<>();
    private final QingPingAccessToken accessToken;
    private final QingPingListDevicesRequest listDevicesRequest;
    private final QingPingJsonDeviceListParser parser;
    private final List<String> deviceMacAddresses;

    public QingPingSensor() throws URISyntaxException {
        this(new QingPingAccessToken(), createListDevicesRequest(), new QingPingJsonDeviceListParser(),
                Arrays.asList(MAC_PRESSURE_DEVICE, MAC_CO2_DEVICE));
    }

    QingPingSensor(QingPingAccessToken accessToken, QingPingListDevicesRequest listDevicesRequest, QingPingJsonDeviceListParser parser,
                   List<String> deviceMacAddresses) {
        this.accessToken = accessToken;
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

    private void doRun() throws CommunicationException {
        final String token = accessToken.getToken();
        final Optional<String> responseOptional = listDevicesRequest.sendRequest(token);
        final String response = responseOptional.orElseThrow(() -> new CommunicationException("QingPing sensor request failed"));
        final List<QingPingSensorData> sensorDataList = new ArrayList<>();
        deviceMacAddresses.forEach(mac -> parser.parseDeviceListResponse(response, mac).ifPresent(sensorDataList::add));
        final Optional<SensorData> sensorDataOptional = getAverageSensorData(sensorDataList);
        final SensorData sensorData = sensorDataOptional.orElseThrow(() -> new CommunicationException("QingPing sensors out of order"));
        notifyObservers(sensorData);
    }

    @Override
    public void addObserver(IndoorSensorObserver observer) {
        observers.add(observer);
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
