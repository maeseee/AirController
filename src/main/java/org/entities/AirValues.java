package org.entities;

public class AirValues {
    private final Humidity humidity;
    private final Temperature temperature;

    public AirValues(Humidity humidity, Temperature temperature) {
        this.humidity = humidity;
        this.temperature = temperature;
    }

    public double getAbsoluteHumidity(){
        return humidity.getAbsoluteHumidity(temperature);
    }

    @Override
    public String toString() {
        return "AirValues{" +
                "humidity=" + humidity +
                ", temperature=" + temperature +
                '}';
    }
}
