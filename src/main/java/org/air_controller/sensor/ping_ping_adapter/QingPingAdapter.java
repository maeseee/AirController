package org.air_controller.sensor.ping_ping_adapter;

import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QingPingAdapter extends ClimateSensor {

    private static final Logger logger = LogManager.getLogger(QingPingAdapter.class);

    private final SensorReducer sensorReducer;
    private final ListDevicesJsonParser parser;

    public QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor sensor) {
        this(persistence, sensor, new SensorReducer(), new ListDevicesJsonParser());
    }

    QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor sensor, SensorReducer sensorReducer, ListDevicesJsonParser parser) {
        super(persistence, sensor);
        this.sensorReducer = sensorReducer;
        this.parser = parser;
    }

    @Override
    protected Optional<ClimateDataPoint> parseResponse(String response) {
        final List<ClimateDataPoint> dataPoints = parse(response);
        return sensorReducer.reduce(dataPoints);
    }

    @Override
    protected String sensorType() {
        return "QingPing";
    }

    private List<ClimateDataPoint> parse(String response) {
        final List<ClimateDataPoint> dataPoints = new ArrayList<>();
        QingPingSensor.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac).ifPresent(dataPoints::add));
        if (dataPoints.isEmpty()) {
            logger.error("No sensor data found in the response: {}", response);
        }
        return dataPoints;
    }
}
