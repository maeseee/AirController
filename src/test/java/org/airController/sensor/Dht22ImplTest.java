package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.SensorValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Dht22ImplTest {

    @Test
    void testWhenAllChecksAreValidThenValueIsPresent() {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final OptionalLong sensorData = createSensorData(23.0, 50.0);
        when(communication.readSensorData()).thenReturn(sensorData);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final SensorValue sensorValue = testee.refreshData();

        assertTrue(sensorValue.getValue().isPresent());
    }

    @Test
    void testWhenCheckInvalidThenRetry3Times() {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final OptionalLong sensorData = createSensorData(23.0, 50.0);
        when(communication.readSensorData()).thenReturn(OptionalLong.empty(), OptionalLong.empty(), OptionalLong.empty(), sensorData);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final SensorValue sensorValue = testee.refreshData();

        assertTrue(sensorValue.getValue().isPresent());
    }

    @Test
    void testWhenWrongChecksumThenInvalid() {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final OptionalLong sensorData = createSensorData(23.0, 50.0);
        final OptionalLong sensorDataInvalid = OptionalLong.of(sensorData.orElse(0) + 1);
        when(communication.readSensorData()).thenReturn(sensorDataInvalid);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final SensorValue sensorValue = testee.refreshData();

        assertFalse(sensorValue.getValue().isPresent());
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

        final SensorValue sensorValue = testee.refreshData();

        assertTrue(sensorValue.getValue().isPresent());
        final AirVO airVO = sensorValue.getValue().get();
        assertEquals(temperature, airVO.getTemperature().getCelsius());
        assertEquals(humidity, airVO.getHumidity().getRelativeHumidity());
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