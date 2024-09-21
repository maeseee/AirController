package org.airController.sensor.dht22;

import org.airController.controllers.SensorData;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Dht22ImplTest {

    @Test
    void testWhenAllChecksAreValidThenValueIsPresent() {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final OptionalLong rawSensorData = createSensorData(23.0, 50.0);
        when(communication.readSensorData()).thenReturn(rawSensorData);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final Optional<SensorData> sensorData = testee.refreshData();

        assertTrue(sensorData.isPresent());
    }

    @Test
    void testWhenCheckInvalidThenRetry3Times() {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final OptionalLong rawSensorData = createSensorData(23.0, 50.0);
        when(communication.readSensorData()).thenReturn(OptionalLong.empty(), OptionalLong.empty(), OptionalLong.empty(), rawSensorData);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final Optional<SensorData> sensorData = testee.refreshData();

        assertTrue(sensorData.isPresent());
    }

    @Test
    void testWhenWrongChecksumThenInvalid() {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final OptionalLong rawSensorData = createSensorData(23.0, 50.0);
        final OptionalLong rawSensorDataInvalid = OptionalLong.of(rawSensorData.orElse(0) + 1);
        when(communication.readSensorData()).thenReturn(rawSensorDataInvalid);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final Optional<SensorData> sensorData = testee.refreshData();

        assertFalse(sensorData.isPresent());
    }

    @ParameterizedTest
    @CsvSource({
            "23.0, 50.0",
            "23.0, 0.0",
            "23.0, 100.0",
            "100.0, 50.0",
            "0.0, 50.0",
            "-10.0, 50.0",
            "-100.0, 50.0",
            "0.0, 50.0",
    })
    void testGetValuesFromData(double temperature, double humidity) {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        when(communication.readSensorData()).thenReturn(createSensorData(temperature, humidity));
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final Optional<SensorData> sensorData = testee.refreshData();

        assertTrue(sensorData.isPresent());
        assertTrue(sensorData.get().getTemperature().isPresent());
        assertTrue(sensorData.get().getHumidity().isPresent());
        final Temperature resultTemperature = sensorData.get().getTemperature().get();
        assertEquals(temperature, resultTemperature.getCelsius());
        assertEquals(humidity, sensorData.get().getHumidity().get().getRelativeHumidity(resultTemperature), 0.001);
    }

    private OptionalLong createSensorData(double temperature, double humidity) {
        long temperatureData = (long) Math.abs(temperature * 10);
        if (temperature < 0) {
            temperatureData |= (1 << 15);
        }
        final long humidityData = (long) (humidity * 10);
        long sensorData = 0;
        sensorData |= (temperatureData << 8);
        sensorData |= (humidityData << 24);

        final long byte0 = (temperatureData >> 8) & 0xFF;
        final long byte1 = temperatureData & 0xFF;
        final long byte2 = (humidityData >> 8) & 0xFF;
        final long byte3 = humidityData & 0xFF;
        sensorData |= ((byte0 + byte1 + byte2 + byte3) & 0xFF);
        return OptionalLong.of(sensorData);
    }
}