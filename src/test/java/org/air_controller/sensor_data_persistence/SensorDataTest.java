package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.sensor_values.*;
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
        final SensorData inputSensorData = new SensorDataBuilder()
                .setTemperatureCelsius(random.nextDouble() * 100)
                .setHumidityRelative(random.nextDouble() * 100)
                .setCo2(random.nextDouble() * 100000)
                .setTime(ZonedDateTime.of(LocalDateTime.of(2024, 9, 27, 20, 51, 12), ZoneOffset.UTC))
                .build();
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
        final SensorData inputSensorData = new SensorDataBuilder()
                .setTemperatureCelsius(celsiusTemperature)
                .setHumidityRelative(relativeHumidity)
                .setCo2(co2Ppm)
                .setTime(time)
                .build();

        testee.persist(inputSensorData);
        final Optional<SensorData> lastSensorData = testee.getMostCurrentSensorData(time.minusMinutes(10));

        assertThat(lastSensorData).hasValueSatisfying(sensorData -> {
            assertThat(sensorData.temperature().celsius()).isCloseTo(celsiusTemperature, within(0.01));
            assertThat(sensorData.humidity().getRelativeHumidity(sensorData.temperature())).isCloseTo(relativeHumidity, within(0.01));
            assertThat(sensorData.co2()).isPresent().hasValueSatisfying(
                    co2 -> assertThat(co2.ppm()).isCloseTo(co2Ppm, within(1.0)));
            assertThat(sensorData.timestamp()).isCloseTo(time, within(1, ChronoUnit.SECONDS));
        });
    }

    private static Stream<Arguments> sensorDataImplementations() {
        return Stream.of(
                Arguments.of(new SensorDataCsv(CSV_FILE_PATH)),
                Arguments.of(new SensorDataDb(new LocalInMemoryDatabase(), TABLE_NAME))
        );
    }
}