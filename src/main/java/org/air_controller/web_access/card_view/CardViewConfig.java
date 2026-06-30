package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardViewConfig {

    @Bean
    ClimateDataPointCardViewService indoorService(@Qualifier("indoorDataPointsAccessor") ClimateDataPointsDbAccessor indoorAccessor) {
        return new ClimateDataPointCardViewService(indoorAccessor);
    }

    @Bean
    ClimateDataPointCardViewService outdoorService(@Qualifier("outdoorDataPointsAccessor") ClimateDataPointsDbAccessor outdoorAccessor) {
        return new ClimateDataPointCardViewService(outdoorAccessor);
    }
}