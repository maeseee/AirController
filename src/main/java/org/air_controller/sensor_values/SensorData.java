package org.air_controller.sensor_values;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Optional;

public record SensorData(Temperature temperature, Humidity humidity, Optional<CarbonDioxide> co2, ZonedDateTime timestamp) {

    @Override
    public @NotNull String toString() {
        return "SensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", timestamp=" + timestamp +
                '}';
    }

    public static SensorData createFromPrimitives(double tempCelsius, double humAbsolute, Double co2Ppm, ZonedDateTime timeStamp) throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(tempCelsius);
        final Humidity humidity = Humidity.createFromAbsolute(humAbsolute);
        final CarbonDioxide co2 = co2Ppm != null ? CarbonDioxide.createFromPpm(co2Ppm) : null;
        return new SensorData(temperature, humidity, Optional.ofNullable(co2), timeStamp);
    }

    public static SensorData createFromPrimitivesWithRelativHumidity(double tempCelsius, double humRelativ, Double co2Ppm, ZonedDateTime timeStamp) throws InvalidArgumentException {
        final Temperature temperature = Temperature.createFromCelsius(tempCelsius);
        final Humidity humidity = Humidity.createFromRelative(humRelativ, temperature);
        final CarbonDioxide co2 = co2Ppm != null ? CarbonDioxide.createFromPpm(co2Ppm) : null;
        return new SensorData(temperature, humidity, Optional.ofNullable(co2), timeStamp);
    }
}
