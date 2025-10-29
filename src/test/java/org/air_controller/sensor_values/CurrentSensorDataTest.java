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
        when(persistence.getMostCurrentSensorData(any())).thenReturn(Optional.empty());
        final CurrentSensorData testee = new CurrentSensorData(persistence);

        assertThat(testee.getCurrentSensorData()).isNotPresent();
    }

    @Test
    void shouldReturnValues() throws InvalidArgumentException {
        final SensorData sensorData = SensorData.createFromPrimitives(20.0, 10.0, 500.0, ZonedDateTime.now(ZoneOffset.UTC));
        when(persistence.getMostCurrentSensorData(any())).thenReturn(Optional.of(sensorData));
        final CurrentSensorData testee = new CurrentSensorData(persistence);

        final Optional<SensorData> currentSensorData = testee.getCurrentSensorData();

        assertThat(currentSensorData).isPresent();
        assertThat(currentSensorData.get().temperature().celsius()).isEqualTo(20.0);
        assertThat(currentSensorData.get().humidity().absoluteHumidity()).isEqualTo(10.0);
        assertThat(currentSensorData.get().co2()).isPresent().hasValueSatisfying(carbonDioxide ->
                assertThat(carbonDioxide.ppm()).isEqualTo(500.0));
    }
}