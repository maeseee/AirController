package org.air_controller;

import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateSensors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    @Bean
    public ClimateDataPointPersistence indoorClimatePersistence() {
        return createClimateSensorAccessor(ClimateDataPointPersistence.INDOOR_TABLE_NAME);
    }

    @Bean("outdoorPersistence")
    public ClimateDataPointPersistence createOutdoorClimateSensorPersistence() {
        return createClimateSensorAccessor(ClimateDataPointPersistence.OUTDOOR_TABLE_NAME);
    }

    @Bean
    public ClimateSensors createSensors(@Qualifier("indoorSensor") ClimateSensor indoor, @Qualifier("outdoorSensor") ClimateSensor outdoor) {
        return new ClimateSensors(indoor, outdoor);
    }

    private ClimateDataPointPersistence createClimateSensorAccessor(String tableName) {
        return new ClimateDataPointsDbAccessor(new MariaDatabase(), tableName);
    }
}
