package org.airController.entities;

public class AirValue {
    private final Temperature temperature;
    private final Humidity humidity;

    public AirValue(Temperature temperature, Humidity humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
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
        return "AirVO{" + temperature + ", " + humidity + '}';
    }
}
