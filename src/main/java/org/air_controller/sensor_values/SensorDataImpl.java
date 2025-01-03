package org.air_controller.sensor_values;

import java.time.ZonedDateTime;
import java.util.Optional;

public class SensorDataImpl implements SensorData {

    private final Temperature temperature;
    private final Humidity humidity;
    private final CarbonDioxide co2;
    private final ZonedDateTime timeStamp;

    public SensorDataImpl(Double tempCelsius, Double humAbsolute, Double co2Ppm, ZonedDateTime timeStamp) throws InvalidArgumentException {
        this.temperature = tempCelsius != null ? Temperature.createFromCelsius(tempCelsius) : null;
        this.humidity = humAbsolute != null ? Humidity.createFromAbsolute(humAbsolute) : null;
        this.co2 = co2Ppm != null ? CarbonDioxide.createFromPpm(co2Ppm) : null;
        this.timeStamp = timeStamp;
    }

    public SensorDataImpl(Temperature temperature, Humidity humidity, CarbonDioxide co2, ZonedDateTime timeStamp) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
        this.timeStamp = timeStamp;
    }

    @Override
    public Optional<Temperature> getTemperature() {
        return Optional.ofNullable(temperature);
    }

    @Override
    public Optional<Humidity> getHumidity() {
        return Optional.ofNullable(humidity);
    }

    @Override
    public Optional<CarbonDioxide> getCo2() {
        return Optional.ofNullable(co2);
    }

    @Override
    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }
}
