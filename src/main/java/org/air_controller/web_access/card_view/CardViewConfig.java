package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardViewConfig {

    @Bean
    ClimateDataPointCardViewService indoorService(ClimateDataPointsDbAccessor indoorDataPointsAccessor) {
        return new ClimateDataPointCardViewService(indoorDataPointsAccessor);
    }

    @Bean
    ClimateDataPointCardViewService outdoorService(ClimateDataPointsDbAccessor outdoorDataPointsAccessor) {
        return new ClimateDataPointCardViewService(outdoorDataPointsAccessor);
    }
}