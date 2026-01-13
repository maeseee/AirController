package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor.ping_ping_adapter.QingPingAdapter;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;

import java.net.URISyntaxException;

import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.INDOOR_TABLE_NAME;

@RequiredArgsConstructor
public class IndoorSensorFactory extends SensorFactory {

    @Override
    protected ClimateSensor createSensor() {
        final ClimateDataPointPersistence persistence = new ClimateDataPointsDbAccessor(new MariaDatabase(), INDOOR_TABLE_NAME);
        try {
            final QingPingSensor sensor = new QingPingSensor();
            return new QingPingAdapter(persistence, sensor);
        } catch (URISyntaxException e) {
            throw new SensorException("Indoor sensor could not be created", e.getCause());
        }
    }
}
