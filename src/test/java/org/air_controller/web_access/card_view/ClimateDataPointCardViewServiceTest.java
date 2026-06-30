package org.air_controller.web_access.card_view;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClimateDataPointCardViewServiceTest {

    @Mock
    private ClimateDataPointsDbAccessor climateDataPointsDbAccessor;

    @Test
    void shouldShowInfo_whenLastUpdateIs11MinutesAgo() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime currentDataPointTime = now.minusMinutes(11);
        final ClimateDataPoint currentDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(22.5)
                .setHumidityRelative(50.0)
                .setTime(currentDataPointTime)
                .build();
        when(climateDataPointsDbAccessor.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.of(currentDataPoint));
        final ClimateDataPointCardViewService testee = new ClimateDataPointCardViewService(climateDataPointsDbAccessor);

        final CardView outdoorCardView = testee.getCardView();

        assertThat(outdoorCardView.info()).contains("11 minutes");
    }

    @Test
    void shouldIgnoreInfo_whenLastUpdateIs9MinutesAgo() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime currentDataPointTime = now.minusMinutes(9);
        final ClimateDataPoint currentDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(22.5)
                .setHumidityRelative(50.0)
                .setTime(currentDataPointTime)
                .build();
        when(climateDataPointsDbAccessor.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.of(currentDataPoint));
        final ClimateDataPointCardViewService testee = new ClimateDataPointCardViewService(climateDataPointsDbAccessor);

        final CardView outdoorCardView = testee.getCardView();

        assertThat(outdoorCardView.info()).isEmpty();
    }

    @Test
    void shouldShowWarning_whenHavingNoSensorValues() {
        when(climateDataPointsDbAccessor.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.empty());
        final ClimateDataPointCardViewService testee = new ClimateDataPointCardViewService(climateDataPointsDbAccessor);

        final CardView outdoorCardView = testee.getCardView();

        assertThat(outdoorCardView.info()).contains("No cards available");
    }
}