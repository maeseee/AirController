package org.air_controller.sensor_data_persistence;

import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.ClimateDataPointBuilder;
import org.air_controller.sensor_values.InvalidArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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

class ClimateDataPointTest {

    private static final String CSV_FILE_PATH = "log/dataPointCsvWriterTest.csv";
    private static final String TABLE_NAME = "TestSensorTable";

    @ParameterizedTest
    @MethodSource("dataPointPersistenceImplementations")
    void shouldWriteDataPointIntoCsvFiles_whenPersist(ClimateDataPointPersistence testee) throws InvalidArgumentException {
        final Random random = new Random();
        final ClimateDataPoint inputClimateDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(random.nextDouble() * 100)
                .setHumidityRelative(random.nextDouble() * 100)
                .setCo2(random.nextDouble() * 100000)
                .setTime(ZonedDateTime.of(LocalDateTime.of(2024, 9, 27, 20, 51, 12), ZoneOffset.UTC))
                .build();
        final int initialSize = testee.read().size();

        testee.persist(inputClimateDataPoint);
        final List<ClimateDataPoint> DataPoints = testee.read();

        assertThat(DataPoints).size().isEqualTo(initialSize + 1);
    }

    @ParameterizedTest
    @MethodSource("dataPointPersistenceImplementations")
    void shouldReadMostCurrentDataPoint(ClimateDataPointPersistence testee) throws InvalidArgumentException {
        final Random random = new Random();
        final double celsiusTemperature = random.nextDouble() * 100;
        final double relativeHumidity = random.nextDouble() * 100;
        final double co2Ppm = random.nextDouble() * 100000;
        final ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        final ClimateDataPoint inputClimateDataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(celsiusTemperature)
                .setHumidityRelative(relativeHumidity)
                .setCo2(co2Ppm)
                .setTime(time)
                .build();

        testee.persist(inputClimateDataPoint);
        final Optional<ClimateDataPoint> lastDataPoint = testee.getMostCurrentClimateDataPoint(time.minusMinutes(10));

        assertThat(lastDataPoint).hasValueSatisfying(dataPoint -> {
            assertThat(dataPoint.temperature().celsius()).isCloseTo(celsiusTemperature, within(0.01));
            assertThat(dataPoint.humidity().getRelativeHumidity(dataPoint.temperature())).isCloseTo(relativeHumidity, within(0.01));
            assertThat(dataPoint.co2()).isPresent().hasValueSatisfying(
                    co2 -> assertThat(co2.ppm()).isCloseTo(co2Ppm, within(1.0)));
            assertThat(dataPoint.timestamp()).isCloseTo(time, within(1, ChronoUnit.SECONDS));
        });
    }

    @ParameterizedTest
    @CsvSource({
            "23, 50, 500, 0, true",
            "24, 50, 500, 0, false",
            "23, 60, 500, 0, false",
            "23, 50, 600, 0, false",
            "23, 50, 500, 1, false",
    })
    void shouldBeDifferent(double comparedTemperature, double comparedHumidity, double comparedCo2, int timeOffset, boolean isEquals)
            throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        final ClimateDataPoint dataPoint1 = new ClimateDataPointBuilder()
                .setTemperatureCelsius(23.0)
                .setHumidityRelative(50.0)
                .setCo2(500.0)
                .setTime(timestamp)
                .build();
        final ClimateDataPoint dataPoint2 = new ClimateDataPointBuilder()
                .setTemperatureCelsius(comparedTemperature)
                .setHumidityRelative(comparedHumidity)
                .setCo2(comparedCo2)
                .setTime(timestamp.plusSeconds(timeOffset))
                .build();

        if (isEquals) {
            assertThat(dataPoint1).isEqualTo(dataPoint2);
        } else {
            assertThat(dataPoint1).isNotEqualTo(dataPoint2);
        }
    }

    @Test
    void shouldConvertToString() throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        final ClimateDataPoint dataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(12.3)
                .setHumidityAbsolute(45.6)
                .setCo2(789.0)
                .setTime(timestamp)
                .build();

        final String dataPointString = dataPoint.toString();

        final String expectedString = "ClimateDataPoint{temperature=12.30\u00B0C, humidity=45.60g/m3, co2=789ppm" + "}";
        assertThat(dataPointString).isEqualTo(expectedString);
    }

    @Test
    void shouldConvertToStringWithoutCo2() throws InvalidArgumentException {
        final ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        final ClimateDataPoint dataPoint = new ClimateDataPointBuilder()
                .setTemperatureCelsius(12.3)
                .setHumidityAbsolute(45.6)
                .setTime(timestamp)
                .build();

        final String dataPointString = dataPoint.toString();

        final String expectedString = "ClimateDataPoint{temperature=12.30\u00B0C, humidity=45.60g/m3" + "}";
        assertThat(dataPointString).isEqualTo(expectedString);
    }

    private static Stream<Arguments> dataPointPersistenceImplementations() {
        return Stream.of(
                Arguments.of(new ClimateDataPointsCsv(CSV_FILE_PATH)),
                Arguments.of(new ClimateDataPointsDb(new LocalInMemoryDatabase(), TABLE_NAME))
        );
    }
}