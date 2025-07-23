package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.SensorData;
import org.air_controller.sensor_values.Temperature;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

class HwSensorData implements SensorData {

    private final Temperature temperature;
    private final Humidity humidity;
    private final CarbonDioxide co2;
    private final ZonedDateTime timestamp;

    public HwSensorData(Temperature temperature, Humidity humidity, @Nullable CarbonDioxide co2, ZonedDateTime timestamp) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
        this.timestamp = timestamp;
    }

    @Override
    public Optional<Temperature> getTemperature() {
        return Optional.of(temperature);
    }

    @Override
    public Optional<Humidity> getHumidity() {
        return Optional.of(humidity);
    }

    @Override
    public Optional<CarbonDioxide> getCo2() {
        return Optional.ofNullable(co2);
    }

    @Override
    public ZonedDateTime getTimeStamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "QingPingSensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", co2=" + co2 +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HwSensorData that)) return false;

        return temperature.equals(that.temperature) && humidity.equals(that.humidity) && Objects.equals(co2, that.co2) &&
                timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, humidity, co2, timestamp);
    }
}
