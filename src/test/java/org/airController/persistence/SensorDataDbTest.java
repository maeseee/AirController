package org.airController.persistence;

import org.airController.sensorValues.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class SensorDataDbTest {
    private final String sensorDataTableName = "TestSensorTable3";

    @Test
    void shouldAddTable() {
        final SensorDataDb testee = new SensorDataDb(sensorDataTableName);

        testee.persist(createSensorData());

        final List<String> entries = testee.read();
        System.out.println(entries);
    }

    private SensorData createSensorData() {
        return new SensorData() {
            @Override public Optional<Temperature> getTemperature() {
                try {
                    return Optional.of(Temperature.createFromCelsius(21.0));
                } catch (InvalidArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override public Optional<Humidity> getHumidity() {
                try {
                    return Optional.of(Humidity.createFromAbsolute(10.0));
                } catch (InvalidArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override public Optional<CarbonDioxide> getCo2() {
                return Optional.empty();
            }

            @Override public LocalDateTime getTimeStamp() {
                return LocalDateTime.now();
            }
        };
    }
}