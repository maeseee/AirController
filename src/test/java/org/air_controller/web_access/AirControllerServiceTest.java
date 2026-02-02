package org.air_controller.web_access;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemPersistenceData;
import org.air_controller.web_access.card.CardGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

        final CardGroup statistics = testee.getStatisticsCardGroup();

        assertThat(statistics.info()).isEmpty();
        assertThat(statistics.cards()).hasSize(1);
        assertThat(statistics.cards().getFirst().name()).contains("24h");
        assertThat(statistics.cards().getFirst().value()).isEqualTo("4.17"); // 1/24 * 100
        assertThat(statistics.cards().getFirst().unit()).isEqualTo("%");
        verifyNoInteractions(indoorDataPointsAccessor);
        verifyNoInteractions(outdoorDataPointsAccessor);
    }

    @Test
    void shouldOnlyGetOnPercentageFromTheLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> actions = List.of(
                new SystemAction(now.minusHours(25), OutputState.ON),
                new SystemAction(now.minusHours(22), OutputState.OFF)
        );
        when(airFlowDbAccessor.getActionsFromTimeToNow(any())).thenReturn(actions);
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor, outdoorDataPointsAccessor);

        final CardGroup statistics = testee.getStatisticsCardGroup();

        assertThat(statistics.info()).isEmpty();
        assertThat(statistics.cards()).hasSize(1);
        assertThat(statistics.cards().getFirst().name()).contains("24h");
        assertThat(statistics.cards().getFirst().value()).isEqualTo("8.33"); // 2/24 * 100
        assertThat(statistics.cards().getFirst().unit()).isEqualTo("%");
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

    @Test
    void shouldShowWarning_whenHavingNoSensorValues() {
        when(outdoorDataPointsAccessor.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.empty());
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor, outdoorDataPointsAccessor);

        final CardGroup outdoorCardGroup = testee.getOutdoorCardGroup();

        assertThat(outdoorCardGroup.info()).contains("No cards available");
        verifyNoInteractions(indoorDataPointsAccessor);
        verifyNoInteractions(airFlowDbAccessor);
    }

    @Test
    void shouldReturnConfidenceCards() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final Map<String, Double> confidences = Map.of("MyTest", 0.5);
        final VentilationSystemPersistenceData data = new VentilationSystemPersistenceData(OutputState.ON, 1.0, confidences, now);
        when(airFlowDbAccessor.getMostCurrentPersistenceData()).thenReturn(Optional.of(data));
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor, outdoorDataPointsAccessor);

        final CardGroup confidenceCardGroup = testee.getConfidenceCardGroup();

        assertThat(confidenceCardGroup.info()).isEqualTo("Total confidence of 1.00");
        assertThat(confidenceCardGroup.cards()).hasSize(1);
        assertThat(confidenceCardGroup.cards().getFirst().name()).isEqualTo("MyTest");
        assertThat(confidenceCardGroup.cards().getFirst().value()).isEqualTo("0.50");
        assertThat(confidenceCardGroup.cards().getFirst().unit()).isEqualTo("");
        verifyNoInteractions(indoorDataPointsAccessor);
        verifyNoInteractions(outdoorDataPointsAccessor);
    }
}