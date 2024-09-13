package org.airController.rules;

import org.airController.controllers.SensorValues;
import org.airController.entities.Humidity;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumidityControlExchangerTest {

    @ParameterizedTest(name = "{index} => humidity %={0}, indoorHumidityAboveOutdoorHumidity = {1}, expectedResult={2}")
    @CsvSource({
            "60, true, -1.0",
            "60, false, 1.0",
            "40, false, -1.0",
            "40, true, 1.0"
    })
    void should(double indoorHumidity, boolean indoorHumidityAboveOutdoorHumidity, double expectedResult) throws IOException {
        when(sensorValues.getIndoorHumidity()).thenReturn(Humidity.createFromRelative(indoorHumidity));
        when(sensorValues.isIndoorHumidityAboveOutdoorHumidity()).thenReturn(indoorHumidityAboveOutdoorHumidity);
        HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensorValues);
        HumidityControlExchanger testee = new HumidityControlExchanger(humidityControlAirFlow);

        Percentage result = testee.turnOn();

        assertThat(result.getPercentage()).isCloseTo(expectedResult, Offset.offset(0.01));
    }

    @Mock
    private SensorValues sensorValues;
}