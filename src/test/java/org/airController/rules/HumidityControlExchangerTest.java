package org.airController.rules;

import org.airController.sensorValues.CurrentSensorValues;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.InvaildArgumentException;
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

    @ParameterizedTest(name = "{index} => humidity %={0}, indoorHumidityAboveOutdoorHumidity = {1}, expectedResult={2}")
    @CsvSource({
            "60, true, -1.0",
            "60, false, 1.0",
            "40, false, -1.0",
            "40, true, 1.0"
    })
    void shouldControlHumidity(double indoorHumidity, boolean indoorHumidityAboveOutdoorHumidity, double expectedResult)
            throws InvaildArgumentException {
        Temperature temperature = Temperature.createFromCelsius(22.0);
        when(sensorValues.getIndoorTemperature()).thenReturn(Optional.of(temperature));
        when(sensorValues.getIndoorHumidity()).thenReturn(Optional.of(Humidity.createFromRelative(indoorHumidity, temperature)));
        when(sensorValues.isIndoorHumidityAboveOutdoorHumidity()).thenReturn(indoorHumidityAboveOutdoorHumidity);
        HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensorValues);
        HumidityControlExchanger testee = new HumidityControlExchanger(humidityControlAirFlow);

        Percentage result = testee.turnOn();

        assertThat(result.getPercentage()).isCloseTo(expectedResult, Offset.offset(0.01));
    }
}