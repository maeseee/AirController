package org.airController.rules;

import org.airController.sensorValues.CurrentSensorValues;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvalidArgumentException;
import org.airController.sensorValues.Temperature;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumidityControlExchangerTest {

    @Mock
    private CurrentSensorValues sensorValues;

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
        when(sensorValues.getIndoorHumidity()).thenReturn(Optional.of(indoorHumidity));
        when(sensorValues.getOutdoorHumidity()).thenReturn(Optional.of(outdoorHumidity));
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensorValues);
        final HumidityControlExchanger testee = new HumidityControlExchanger(humidityControlAirFlow);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.getWeightedConfidenceValue()).isCloseTo(expectedResult, Offset.offset(0.01));
    }
}