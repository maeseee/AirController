package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor.ping_ping_adapter.QingPingAdapter;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_data_persistence.ClimateDataPoints;
import org.air_controller.sensor_data_persistence.ClimateDataPointsCsv;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDb;

import java.net.URISyntaxException;
import java.util.List;

import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.INDOOR_SENSOR_CSV_PATH;
import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.INDOOR_TABLE_NAME;

@RequiredArgsConstructor
public class IndoorSensorFactory extends SensorFactory {

    @Override
    protected ClimateSensor createSensor() {
        final ClimateDataPointPersistence persistence = new ClimateDataPoints(List.of(
                new ClimateDataPointsDb(new MariaDatabase(), INDOOR_TABLE_NAME),
                new ClimateDataPointsCsv(INDOOR_SENSOR_CSV_PATH)));
        try {
            final QingPingSensor sensor = new QingPingSensor();
            return new QingPingAdapter(persistence, sensor);
        } catch (URISyntaxException e) {
            throw new SensorException("Indoor sensor could not be created", e.getCause());
        }
    }
}
