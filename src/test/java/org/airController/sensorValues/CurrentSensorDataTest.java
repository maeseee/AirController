package org.airController.sensorValues;

import org.airController.sensorDataPersistence.SensorDataPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
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

        assertThat(testee.getTemperature()).isNotPresent();
        assertThat(testee.getHumidity()).isNotPresent();
        assertThat(testee.getCo2()).isNotPresent();
    }

    @Test
    void shouldReturnValues() throws InvalidArgumentException {
        final SensorDataImpl sensorData = new SensorDataImpl(20.0, 10.0, 500.0, ZonedDateTime.now(ZoneId.of("UTC")));
        when(persistence.getMostCurrentSensorData(any())).thenReturn(Optional.of(sensorData));
        final CurrentSensorData testee = new CurrentSensorData(persistence);

        assertThat(testee.getTemperature()).isPresent().hasValueSatisfying(temperature ->
                assertThat(temperature.getCelsius()).isEqualTo(20.0));
        assertThat(testee.getHumidity()).isPresent().hasValueSatisfying(humidity ->
                assertThat(humidity.getAbsoluteHumidity()).isEqualTo(10.0));
        assertThat(testee.getCo2()).isPresent().hasValueSatisfying(carbonDioxide ->
                assertThat(carbonDioxide.getPpm()).isEqualTo(500.0));
    }
}