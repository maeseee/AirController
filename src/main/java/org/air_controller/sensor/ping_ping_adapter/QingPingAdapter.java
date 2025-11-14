package org.air_controller.sensor.ping_ping_adapter;

import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class QingPingAdapter extends ClimateSensor {

    private static final Logger logger = LogManager.getLogger(QingPingAdapter.class);

    private final QingPingSensor qingPingSensor;
    private final SensorReducer sensorReducer = new SensorReducer();

    public QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor qingPingSensor) {
        super(persistence);
        this.qingPingSensor = qingPingSensor;
    }

    @Override
    public void run() {
        try {
            final List<ClimateDataPoint> climateDataPoints = qingPingSensor.readData();
            final ClimateDataPoint dataPoint = sensorReducer.reduce(climateDataPoints);
            persistDataPoint(dataPoint);
        } catch (Exception exception) {
            logger.error("Exception in QingPing sensor loop:", exception);
        }
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
