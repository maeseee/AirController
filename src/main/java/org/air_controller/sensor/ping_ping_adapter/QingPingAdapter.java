package org.air_controller.sensor.ping_ping_adapter;

import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class QingPingAdapter extends ClimateSensor {

    private static final Logger logger = LogManager.getLogger(QingPingAdapter.class);

    private final QingPingSensor qingPingSensor;
    private final SensorReducer sensorReducer;
    private final ListDevicesJsonParser parser;

    public QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor qingPingSensor) {
        this(persistence, qingPingSensor, new SensorReducer(), new ListDevicesJsonParser());
    }

    QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor qingPingSensor, SensorReducer sensorReducer, ListDevicesJsonParser parser) {
        super(persistence);
        this.qingPingSensor = qingPingSensor;
        this.sensorReducer = sensorReducer;
        this.parser = parser;
    }

    @Override
    public void run() {
        try {
            final String response = qingPingSensor.readData();
            final List<ClimateDataPoint> climateDataPoints = parseResponse(response);
            final ClimateDataPoint dataPoint = sensorReducer.reduce(climateDataPoints);
            persistDataPoint(dataPoint);
        } catch (Exception exception) {
            logger.error("Exception in QingPing sensor loop:", exception);
        }
    }

    private List<ClimateDataPoint> parseResponse(String response) {
        final List<ClimateDataPoint> climateDataPoints = new ArrayList<>();
        QingPingSensor.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac).ifPresent(climateDataPoints::add));
        if (climateDataPoints.isEmpty()) {
            logger.error("No sensor data found in the response: {}", response);
        }
        return climateDataPoints;
    }

//    @Override
//    public Optional<ClimateDataPoint> readDataPoint() {
//        final String sensorValues = qingPingSensor.readSensor();
//        return Optional.empty();
//    }

    private void persistDataPoint(ClimateDataPoint dataPoint) {
        logger.info("New data point: {}", dataPoint);
        persistence.persist(dataPoint);
    }
}
