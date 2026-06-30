package org.air_controller.web_access.graph;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphConfig {

    @Bean("indoorGraphService")
    SensorGraphService createIndoorGraphService(@Qualifier("indoorDataPointsAccessor") ClimateDataPointsDbAccessor dbAccessor) {
        return new SensorGraphService(dbAccessor);
    }

    @Bean("outdoorGraphService")
    SensorGraphService createOutdoorGraphService(@Qualifier("outdoorDataPointsAccessor") ClimateDataPointsDbAccessor dbAccessor) {
        return new SensorGraphService(dbAccessor);
    }
}