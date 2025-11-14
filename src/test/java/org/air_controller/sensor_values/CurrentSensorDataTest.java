package org.air_controller.sensor_values;

import org.air_controller.sensor_data_persistence.SensorDataPersistence;
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
class CurrentSensorDataTest {
    @Mock
    private SensorDataPersistence persistence;

    @Test
    void shouldReturnEmpty_whenValuesMissing() {
        when(persistence.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.empty());
        final CurrentSensorData testee = new CurrentSensorData(persistence);

        assertThat(testee.getCurrentClimateDataPoint()).isNotPresent();
    }

    @Test
    void shouldReturnValues() throws InvalidArgumentException {
        final ClimateDataPoint dataPoint = new SensorDataBuilder()
                .setTemperatureCelsius(20.0)
                .setHumidityAbsolute(10.0)
                .setCo2(500.0)
                .setTime(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
        when(persistence.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.of(dataPoint));
        final CurrentSensorData testee = new CurrentSensorData(persistence);

        final Optional<ClimateDataPoint> currentSensorData = testee.getCurrentClimateDataPoint();

        assertThat(currentSensorData).isPresent();
        assertThat(currentSensorData.get().temperature().celsius()).isEqualTo(20.0);
        assertThat(currentSensorData.get().humidity().absoluteHumidity()).isEqualTo(10.0);
        assertThat(currentSensorData.get().co2()).isPresent().hasValueSatisfying(carbonDioxide ->
                assertThat(carbonDioxide.ppm()).isEqualTo(500.0));
    }
}