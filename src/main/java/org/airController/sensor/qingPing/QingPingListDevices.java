package org.airController.sensor.qingPing;

import org.airController.sensorValues.CarbonDioxide;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.Temperature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

class QingPingListDevices {
    private static final Logger logger = LogManager.getLogger(QingPingListDevices.class);

    private final QingPingListDevicesRequest listDevicesRequest;
    private final QingPingListDevicesJsonParser parser;

    public QingPingListDevices() throws URISyntaxException {
        this(createListDevicesRequest(), new QingPingListDevicesJsonParser());
    }

    QingPingListDevices(QingPingListDevicesRequest listDevicesRequest, QingPingListDevicesJsonParser parser) {
        this.listDevicesRequest = listDevicesRequest;
        this.parser = parser;
    }

    public List<QingPingSensorData> readSensorDataList(String token) throws CommunicationException, IOException, URISyntaxException {
        final String response = listDevicesRequest.sendRequest(token);
        final List<QingPingSensorData> sensorDataList = new ArrayList<>();
        QingPingDevices.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac)
                        .ifPresent(sensorData -> sensorDataList.add(fixSensorOffset(sensorData, mac))));
        return sensorDataList;
    }

    private QingPingSensorData fixSensorOffset(QingPingSensorData sensorData, String mac) {
        if (mac.equals(QingPingDevices.MAC_CO2_DEVICE_1)) {
            return fixHumidity(sensorData, -0.74); // Is about -4%
        }
        return sensorData;
    }

    private QingPingSensorData fixHumidity(QingPingSensorData sensorData, double absoluteHumidityOffset) {
        final Temperature temperature = sensorData.getTemperature().orElse(null);
        final CarbonDioxide co2 = sensorData.getCo2().orElse(null);
        try {
            final Humidity humidity = sensorData.getHumidity().orElseThrow(() -> new InvalidArgumentException("Not Possible"));
            final double absoluteHumidity = humidity.getAbsoluteHumidity();
            final Humidity updatedHumidity = Humidity.createFromAbsolute(absoluteHumidity + absoluteHumidityOffset);
            return new QingPingSensorData(temperature, updatedHumidity, co2, sensorData.getTimeStamp());
        } catch (InvalidArgumentException e) {
            logger.error("Invalid Humidity: {}", e.getMessage());
            return sensorData;
        }
    }

    private static QingPingListDevicesRequest createListDevicesRequest() throws URISyntaxException {
        final String urlString = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(urlString);
        return new QingPingListDevicesRequest(uri);
    }
}
