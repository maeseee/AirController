package org.air_controller.web_access;

import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemPart;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.INDOOR_TABLE_NAME;
import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.OUTDOOR_TABLE_NAME;

@Configuration
public class DbAccessorConfig {

    @Bean
    public SystemActionDbAccessor airFlowDbAccessor() {
        return new SystemActionDbAccessor(new MariaDatabase(), SystemPart.AIR_FLOW);
    }

    @Bean
    public ClimateDataPointsDbAccessor indoorDataPointsAccessor() {
        return new ClimateDataPointsDbAccessor(new MariaDatabase(), INDOOR_TABLE_NAME);
    }

    @Bean
    public ClimateDataPointsDbAccessor outdoorDataPointsAccessor() {
        return new ClimateDataPointsDbAccessor(new MariaDatabase(), OUTDOOR_TABLE_NAME);
    }
}
