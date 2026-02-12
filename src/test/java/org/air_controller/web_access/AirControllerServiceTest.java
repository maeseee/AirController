package org.air_controller.web_access;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.web_access.graph.GraphView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirControllerServiceTest {
    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;
    @Mock
    private ClimateDataPointsDbAccessor indoorDataPointsAccessor;

    @Test
    void shouldReturnTemperatureGraphView_whenAskedFor() throws InvalidArgumentException {
        final List<ClimateDataPoint> dataPoints = createClimateDataPoints();
        when(indoorDataPointsAccessor.getDataPointsFromLast24Hours()).thenReturn(dataPoints);
        final AirControllerService testee = new AirControllerService(airFlowDbAccessor, indoorDataPointsAccessor);

        final GraphView graphView = testee.getIndoorTemperatureGraph();

        assertThat(graphView.nameWithUnit()).isEqualTo("Temperature (°C)");
        assertThat(graphView.items()).hasSize(2);
        assertThat(graphView.items().get(0).value()).isEqualTo(20.0);
        assertThat(graphView.items().get(0).time()).isEqualTo(LocalDateTime.of(2026, 11, 2, 6 + timeDifferenceToUtc(), 0, 0));
        assertThat(graphView.items().get(1).value()).isEqualTo(30.0);
        assertThat(graphView.items().get(1).time()).isEqualTo(LocalDateTime.of(2026, 11, 2, 7 + timeDifferenceToUtc(), 0, 0));
    }

    private List<ClimateDataPoint> createClimateDataPoints() throws InvalidArgumentException {
        final ZonedDateTime time = ZonedDateTime.of(2026, 11, 2, 7, 0, 0, 0, ZoneOffset.UTC);
        final ClimateDataPoint dataPoint1 = new ClimateDataPointBuilder()
                .setTemperatureCelsius(20.0)
                .setHumidityRelative(80.0)
                .setTime(time.minusHours(1))
                .build();
        final ClimateDataPoint dataPoint2 = new ClimateDataPointBuilder()
                .setTemperatureCelsius(30.0)
                .setHumidityRelative(40.0)
                .setTime(time)
                .build();
        return List.of(dataPoint1, dataPoint2);
    }

    private int timeDifferenceToUtc() {
        final ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        return (int) Duration.between(time.toLocalDateTime(), time.withZoneSameInstant(ZoneId.of("Europe/Berlin")).toLocalDateTime()).toHours();
    }
}