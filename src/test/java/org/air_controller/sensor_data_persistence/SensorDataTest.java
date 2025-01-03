package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.*;
import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SensorDataTest {

    private static final String CSV_FILE_PATH = "log/sensorValueCsvWriterTest.csv";
    private static final String TABLE_NAME = "TestSensorTable";

    @ParameterizedTest
    @MethodSource("sensorDataImplementations")
    void shouldWriteSensorDataIntoCsvFiles_whenPersist(SensorDataPersistence testee) throws InvalidArgumentException {
        final Random random = new Random();
        final double celsiusTemperature = random.nextDouble() * 100;
        final double relativeHumidity = random.nextDouble() * 100;
        final double co2Ppm = random.nextDouble() * 100000;
        final ZonedDateTime time = ZonedDateTime.of(LocalDateTime.of(2024, 9, 27, 20, 51, 12), ZoneOffset.UTC);
        final SensorData inputSensorData = createSensorData(celsiusTemperature, relativeHumidity, co2Ppm, time);
        final int initialSize = testee.read().size();

        testee.persist(inputSensorData);
        final List<SensorData> sensorData = testee.read();

        assertThat(sensorData).size().isEqualTo(initialSize + 1);
    }

    @ParameterizedTest
    @MethodSource("sensorDataImplementations")
    void shouldReadMostCurrentSensorData(SensorDataPersistence testee) throws InvalidArgumentException {
        final Random random = new Random();
        final double celsiusTemperature = random.nextDouble() * 100;
        final double relativeHumidity = random.nextDouble() * 100;
        final double co2Ppm = random.nextDouble() * 100000;
        final ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        final SensorData inputSensorData = createSensorData(celsiusTemperature, relativeHumidity, co2Ppm, time);

        testee.persist(inputSensorData);
        final Optional<SensorData> lastSensorData = testee.getMostCurrentSensorData(time.minusMinutes(10));

        assertThat(lastSensorData).hasValueSatisfying(sensorData -> {
            assertThat(sensorData.getTemperature()).isPresent().hasValueSatisfying(
                    temperature -> assertThat(temperature.getCelsius()).isCloseTo(temperature.getCelsius(), Offset.offset(0.01)));
            assertThat(sensorData.getHumidity()).isPresent().hasValueSatisfying(
                    humidity -> assertThat(humidity.getRelativeHumidity(sensorData.getTemperature().get())).isCloseTo(relativeHumidity,
                            Offset.offset(0.01)));
            assertThat(sensorData.getCo2()).isPresent().hasValueSatisfying(
                    co2 -> assertThat(co2.getPpm()).isCloseTo(co2Ppm, Offset.offset(1.0)));
            assertThat(sensorData.getTimeStamp()).isCloseTo(time, within(1, ChronoUnit.SECONDS));
        });
    }

    private static Stream<Arguments> sensorDataImplementations() {
        return Stream.of(
                Arguments.of(new SensorDataCsv(CSV_FILE_PATH)),
                Arguments.of(new SensorDataDb(TABLE_NAME))
        );
    }

    private SensorData createSensorData(double celsiusTemperatur, double relativeHumidity, double co2Ppm, ZonedDateTime time)
            throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(celsiusTemperatur);
        final Humidity humidity = Humidity.createFromRelative(relativeHumidity, temperature);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Ppm);
        return new SensorDataImpl(temperature, humidity, co2, time);
    }
}