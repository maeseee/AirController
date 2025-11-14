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

    private final QingPingSensor sensor;
    private final SensorReducer sensorReducer;
    private final ListDevicesJsonParser parser;

    public QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor sensor) {
        this(persistence, sensor, new SensorReducer(), new ListDevicesJsonParser());
    }

    QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor sensor, SensorReducer sensorReducer, ListDevicesJsonParser parser) {
        super(persistence);
        this.sensor = sensor;
        this.sensorReducer = sensorReducer;
        this.parser = parser;
    }

    @Override
    public void run() {
        try {
            final String response = sensor.readData();
            Optional<ClimateDataPoint> dataPoint = parseResponse(response);
            dataPoint.ifPresent(this::persistDataPoint);
        } catch (Exception exception) {
            logger.error("Exception in QingPing sensor loop:", exception);
        }
    }

    protected Optional<ClimateDataPoint> parseResponse(String response) {
        final List<ClimateDataPoint> climateDataPoints = parse(response);
        return sensorReducer.reduce(climateDataPoints);
    }

    private List<ClimateDataPoint> parse(String response) {
        final List<ClimateDataPoint> climateDataPoints = new ArrayList<>();
        QingPingSensor.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac).ifPresent(climateDataPoints::add));
        if (climateDataPoints.isEmpty()) {
            logger.error("No sensor data found in the response: {}", response);
        }
        return climateDataPoints;
    }

    private void persistDataPoint(ClimateDataPoint dataPoint) {
        logger.info("New data point: {}", dataPoint);
        persistence.persist(dataPoint);
    }
}
