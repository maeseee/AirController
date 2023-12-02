package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Dht22ImplTest {

    @Test
    void testWhenAllChecksAreValidThenValueIsPresent() throws IOException {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final Optional<String> sensorData = createSensorData(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(50.0));
        when(communication.readSensorData()).thenReturn(sensorData);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final Optional<AirValue> airValue = testee.refreshData();

        assertTrue(airValue.isPresent());
    }

    @Test
    void testWhenCheckInvalidThenRetry3Times() throws IOException {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final Optional<String> sensorData = createSensorData(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(50.0));
        when(communication.readSensorData()).thenReturn(Optional.empty(), Optional.empty(), Optional.empty(), sensorData);
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final Optional<AirValue> airValue = testee.refreshData();

        assertTrue(airValue.isPresent());
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
    void testGetValuesFromData(double temp, double hum) throws IOException {
        final OneWireCommunication communication = mock(OneWireCommunication.class);
        final Temperature temperature = Temperature.createFromCelsius(temp);
        final Humidity humidity = Humidity.createFromRelative(hum);
        when(communication.readSensorData()).thenReturn(createSensorData(temperature, humidity));
        final Dht22Impl testee = new Dht22Impl(communication, 0);

        final Optional<AirValue> airValue = testee.refreshData();

        assertTrue(airValue.isPresent());
        assertEquals(temperature.getCelsius(), airValue.get().getTemperature().getCelsius(), 0.01);
        assertEquals(humidity.getRelativeHumidity(), airValue.get().getHumidity().getRelativeHumidity(),0.01);
    }

    private Optional<String> createSensorData(Temperature temperature, Humidity humidity) {
        final String json = """
                {
                "main":{"temp":%.2f,"humidity":%.2f}
                }
                """;
        final String jsonFormatted = String.format(json, temperature.getKelvin(), humidity.getRelativeHumidity());
        return Optional.of(jsonFormatted);
    }
}