package org.air_controller;

import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor.open_weather_api_adapter.OpenWeatherApiAdapter;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor.qing_ping_adapter.QingPingAdapter;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_data_persistence.ClimateSensorAccessors;
import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemPart;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    @Bean("airFlowAccessor")
    public SystemActionDbAccessor createAirFlowSystemActionDbAccessor() {
        return createSystemActionDbAccessor(SystemPart.AIR_FLOW);
    }

    @Bean("humidityAccessor")
    public SystemActionDbAccessor createHumiditySystemActionDbAccessor() {
        return createSystemActionDbAccessor(SystemPart.HUMIDITY);
    }

    @Bean("indoorPersistence")
    public ClimateDataPointPersistence createIndoorClimateSensorPersistence() {
        return createClimateSensorAccessor(ClimateDataPointPersistence.INDOOR_TABLE_NAME);
    }

    @Bean("outdoorPersistence")
    public ClimateDataPointPersistence createOutdoorClimateSensorPersistence() {
        return createClimateSensorAccessor(ClimateDataPointPersistence.OUTDOOR_TABLE_NAME);
    }

    @Bean
    public ClimateSensorAccessors createClimateSensorAccessors(
            @Qualifier("indoorPersistence") ClimateDataPointPersistence indoorPersistence,
            @Qualifier("outdoorPersistence") ClimateDataPointPersistence outdoorPersistence) {
        return new ClimateSensorAccessors(indoorPersistence, outdoorPersistence);
    }

    @Bean("indoorSensor")
    public ClimateSensor createIndoorSensor(@Qualifier("indoorPersistence") ClimateDataPointPersistence persistence, QingPingSensor sensor) {
        return new QingPingAdapter(persistence, sensor);
    }

    @Bean("outdoorSensor")
    public ClimateSensor createOutdoorSensor(@Qualifier("outdoorPersistence") ClimateDataPointPersistence persistence, OpenWeatherApiSensor sensor) {
        return new OpenWeatherApiAdapter(persistence, sensor);
    }

    @Bean
    public ClimateSensors createSensors(@Qualifier("indoorSensor") ClimateSensor indoor, @Qualifier("outdoorSensor") ClimateSensor outdoor) {
        return new ClimateSensors(indoor, outdoor);
    }

    private SystemActionDbAccessor createSystemActionDbAccessor(SystemPart systemPart) {
        return new SystemActionDbAccessor(new MariaDatabase(), systemPart);
    }

    private ClimateDataPointPersistence createClimateSensorAccessor(String tableName) {
        return new ClimateDataPointsDbAccessor(new MariaDatabase(), tableName);
    }
}
