package org.air_controller.sensor;

import lombok.RequiredArgsConstructor;
import org.air_controller.sensor.ping_ping_adapter.QingPingAdapter;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;

import java.net.URISyntaxException;

@RequiredArgsConstructor
public class IndoorSensorFactory extends SensorFactory {

    @Override
    protected ClimateSensor createSensor(ClimateDataPointPersistence persistence) {
        try {
            final QingPingSensor sensor = new QingPingSensor();
            return new QingPingAdapter(persistence, sensor);
        } catch (URISyntaxException e) {
            throw new SensorException("Indoor sensor could not be created", e.getCause());
        }
    }
}
