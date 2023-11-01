package org.airController.entities;

public class AirVO {
    private final Temperature temperature;
    private final Humidity humidity;

    public AirVO(Temperature temperature, Humidity humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public AirVO(double temperatureCelsius, double relativeHumidity) {
        this.temperature = Temperature.createFromCelsius(temperatureCelsius);
        this.humidity = Humidity.createFromRelative(relativeHumidity);
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Humidity getHumidity() {
        return humidity;
    }

    public double getAbsoluteHumidity() {
        return humidity.getAbsoluteHumidity(temperature);
    }

    @Override
    public String toString() {
        return "AirValues{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
