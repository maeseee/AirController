package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.SensorValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Dht22ImplTest {

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
        final int[] sensorData = {0, 0, 0, 0, 0};
        setTemperature(sensorData, temperature);
        setHumidity(sensorData, humidity);
        setParity(sensorData);
        when(communication.readSensorData()).thenReturn(40);
        when(communication.getSensorData()).thenReturn(sensorData);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final SensorValue sensorValue = testee.refreshData();

        assertTrue(sensorValue.getValue().isPresent());
        final AirVO airVO = sensorValue.getValue().get();
        assertEquals(temperature, airVO.getTemperature().getCelsius());
        assertEquals(humidity, airVO.getHumidity().getRelativeHumidity());
    }

    private void setTemperature(int[] sensorData, double temperature) {
        final int intTemperature = (int) Math.abs(temperature * 10);
        sensorData[3] = intTemperature & 0xFF;
        sensorData[2] = (intTemperature >> 8) & 0x7F;
        if (temperature < 0) {
            sensorData[2] |= 0x80;
        }
    }

    private void setHumidity(int[] sensorData, double humidity) {
        final int intHumidity = (int) (humidity * 10);
        sensorData[1] = intHumidity & 0xFF;
        sensorData[0] = (intHumidity >> 8) & 0xFF;
    }

    private void setParity(int[] sensorData) {
        sensorData[4] = (sensorData[0] + sensorData[1] + sensorData[2] + sensorData[3]) & 0xFF;
    }

}