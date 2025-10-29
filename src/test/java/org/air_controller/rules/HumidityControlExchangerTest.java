package org.air_controller.rules;

import org.air_controller.sensor_values.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumidityControlExchangerTest {

    @Mock
    private CurrentSensorData currentIndoorSensorData;
    @Mock
    private CurrentSensorData currentOutdoorSensorData;

    @ParameterizedTest(name = "{index} => indoorHumidity={0}%, outdoorHumidity={1}%, expectedResult={2}")
    @CsvSource({
            "52.0, 65.0, -1.0",
            "60.0, 52.5, -1.0",
            "45.0, 52.5, -1.0",
            "45.0, 65.0, -1.0",
            "40.0, 27.5, 1.0",
            "65.0, 77.5, 1.0",
    })
    void shouldControlHumidityExchanger(double indoorHumidityValue, double outdoorHumidityValue,
            double expectedResult)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(22.0);
        final Humidity indoorHumidity = Humidity.createFromRelative(indoorHumidityValue, temperature);
        final Humidity outdoorHumidity = Humidity.createFromRelative(outdoorHumidityValue, temperature);
        final SensorData indoorSensorData = new SensorData(temperature, indoorHumidity, Optional.empty(), ZonedDateTime.now(ZoneOffset.UTC));
        final SensorData outdoorSensorData = new SensorData(temperature, outdoorHumidity, Optional.empty(), ZonedDateTime.now(ZoneOffset.UTC));
        when(currentIndoorSensorData.getCurrentSensorData()).thenReturn(Optional.of(indoorSensorData));
        when(currentOutdoorSensorData.getCurrentSensorData()).thenReturn(Optional.of(outdoorSensorData));
        final HumidityControlExchanger testee = new HumidityControlExchanger(currentIndoorSensorData, currentOutdoorSensorData);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, within(0.01));
    }
}