package org.air_controller.web_access.graph;

import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.air_controller.sensor_values.MeasuredValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndoorSensorGraphServiceTest {
    @Mock
    private ClimateDataPointPersistence persistence;

    @Test
    void shouldReturnTemperatureGraphView_whenAskedFor() throws InvalidArgumentException {
        final List<ClimateDataPoint> dataPoints = createClimateDataPoints();
        when(persistence.getDataPoints(any())).thenReturn(dataPoints);
        final SensorGraphService testee = new SensorGraphService(persistence);

        final GraphView graphView = testee.getGraphView(MeasuredValue.TEMPERATURE, Duration.ofHours(24), "Test");

        assertThat(graphView.nameWithUnit()).isEqualTo("Temperature (°C)");
        assertThat(graphView.items()).hasSize(2);
        assertThat(graphView.items().get(0).value()).isEqualTo(20.0);
        assertThat(graphView.items().get(0).timeStamp()).isEqualTo(LocalDateTime.of(2026, 1, 15, 6 + timeDifferenceToUtc(), 0, 0));
        assertThat(graphView.items().get(1).value()).isEqualTo(30.0);
        assertThat(graphView.items().get(1).timeStamp()).isEqualTo(LocalDateTime.of(2026, 1, 15, 7 + timeDifferenceToUtc(), 0, 0));
    }

    private List<ClimateDataPoint> createClimateDataPoints() throws InvalidArgumentException {
        final ZonedDateTime time = ZonedDateTime.of(2026, 1, 15, 7, 0, 0, 0, ZoneOffset.UTC);
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