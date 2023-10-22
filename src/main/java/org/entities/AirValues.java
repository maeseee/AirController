package org.entities;

public class AirValues {
    private final Temperature temperature;
    private final Humidity humidity;

    public AirValues(Temperature temperature, Humidity humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public AirValues(double temperatureCelsius, double relativeHumidity) {
        this.temperature = new Temperature(temperatureCelsius);
        this.humidity = new Humidity(relativeHumidity);
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
