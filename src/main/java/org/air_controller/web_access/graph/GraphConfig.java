package org.air_controller.web_access.graph;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphConfig {

    @Bean("indoorGraphService")
    SensorGraphService createIndoorGraphService(ClimateDataPointsDbAccessor indoorDataPointsAccessor) {
        return new SensorGraphService(indoorDataPointsAccessor);
    }

    @Bean("outdoorGraphService")
    SensorGraphService createOutdoorGraphService(ClimateDataPointsDbAccessor outdoorDataPointsAccessor) {
        return new SensorGraphService(outdoorDataPointsAccessor);
    }
}