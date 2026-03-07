package org.air_controller.rules.airflow;

import org.air_controller.rules.Confidence;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.sensor_values.InvalidArgumentException;
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
    private ClimateSensor indoor;
    @Mock
    private ClimateSensor outdoor;

    @ParameterizedTest(name = "{index} => indoorHumidity={0}%, outdoorHumidity={1}%, expectedResult={2}")
    @CsvSource({
            "60.0, 52.5",
            "45.0, 52.5",
            "45.0, 65.0",
    })
    void shouldControlHumidityExchanger_whenOff(double relativeIndoorHumidity, double relativeOutdoorHumidity)
            throws InvalidArgumentException {
        final HumidityControlRule testee = createHumidityControlExchanger(relativeIndoorHumidity, relativeOutdoorHumidity);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.value()).isNegative();
    }

    @ParameterizedTest(name = "{index} => indoorHumidity={0}%, outdoorHumidity={1}%, expectedResult={2}")
    @CsvSource({
            "40.0, 27.5",
            "65.0, 77.5",
    })
    void shouldControlHumidityExchanger_whenOn(double relativeIndoorHumidity, double relativeOutdoorHumidity)
            throws InvalidArgumentException {
        final HumidityControlRule testee = createHumidityControlExchanger(relativeIndoorHumidity, relativeOutdoorHumidity);

        final Confidence result = testee.turnOnConfidence();

        assertThat(result.value()).isPositive();
    }

    private HumidityControlRule createHumidityControlExchanger(double relativeIndoorHumidity, double relativeOutdoorHumidity)
            throws InvalidArgumentException {
        final ClimateDataPoint indoorDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(22.0)
                .setHumidityRelative(relativeIndoorHumidity)
                .build();
        final ClimateDataPoint outdoorClimateDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(22.0)
                .setHumidityRelative(relativeOutdoorHumidity)
                .build();
        when(indoor.getCurrentDataPoint()).thenReturn(Optional.of(indoorDataPoint));
        when(outdoor.getCurrentDataPoint()).thenReturn(Optional.of(outdoorClimateDataPoint));
        final ClimateSensors sensors = new ClimateSensors(indoor, outdoor);
        return new HumidityControlRule(sensors);
    }
}