package org.air_controller.web_access;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirControllerServiceTest {

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;
    @Mock
    private ClimateDataPointsDbAccessor indoorDataPointsAccessor;
    @Mock
    private ClimateDataPointsDbAccessor outdoorDataPointsAccessor;

    @Test
    void shouldGetOnPercentageFromTheLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> actions = List.of(
                new SystemAction(now.minusHours(10), OutputState.ON),
                new SystemAction(now.minusHours(9), OutputState.OFF)
        );
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(actions);
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor, outdoorDataPointsAccessor);

        final double percentage = testee.getOnPercentageFromTheLast24Hours();

        assertThat(percentage).isCloseTo(1.0 / 12.0, within(0.1));
        verifyNoInteractions(indoorDataPointsAccessor);
        verifyNoInteractions(outdoorDataPointsAccessor);
    }

    @Test
    void shouldOnlyGetOnPercentageFromTheLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> actions = List.of(
                new SystemAction(now.minusHours(13), OutputState.ON),
                new SystemAction(now.minusHours(10), OutputState.OFF)
        );
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(actions);
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor, outdoorDataPointsAccessor);

        final double percentage = testee.getOnPercentageFromTheLast24Hours();

        assertThat(percentage).isCloseTo(2.0 / 12.0, within(0.1));
        verifyNoInteractions(indoorDataPointsAccessor);
        verifyNoInteractions(outdoorDataPointsAccessor);
    }

    @Test
    void shouldShowInfo_whenLastUpdateIs11MinutesAgo() throws InvalidArgumentException {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime currentDataPointTime = now.minusMinutes(11);
        final ClimateDataPoint currentDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(22.5)
                .setHumidityRelative(50.0)
                .setTime(currentDataPointTime)
                .build();
        when(outdoorDataPointsAccessor.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.of(currentDataPoint));
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor, outdoorDataPointsAccessor);

        final CardGroup outdoorCardGroup = testee.getOutdoorCardGroup();

        assertThat(outdoorCardGroup.info()).contains("11 minutes");
        verifyNoInteractions(indoorDataPointsAccessor);
        verifyNoInteractions(airFlowDbAccessor);
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
        when(outdoorDataPointsAccessor.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.of(currentDataPoint));
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor, outdoorDataPointsAccessor);

        final CardGroup outdoorCardGroup = testee.getOutdoorCardGroup();

        assertThat(outdoorCardGroup.info()).isEmpty();
        verifyNoInteractions(indoorDataPointsAccessor);
        verifyNoInteractions(airFlowDbAccessor);
    }
}