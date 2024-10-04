package org.airController.sensorValues;

import java.time.LocalDateTime;
import java.util.Optional;

public class SensorDataImpl implements SensorData {

    private final Temperature temperature;
    private final Humidity humidity;
    private final CarbonDioxide co2;
    private final LocalDateTime timeStamp;

    public SensorDataImpl(double tempCelsius, double humAbsolute, double co2Ppm, LocalDateTime timeStamp) throws InvalidArgumentException {
        this.temperature = Temperature.createFromCelsius(tempCelsius);
        this.humidity = Humidity.createFromAbsolute(humAbsolute);
        this.co2 = CarbonDioxide.createFromPpm(co2Ppm);
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
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
