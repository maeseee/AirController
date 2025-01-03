package org.air_controller.sensor.dht22;

import org.air_controller.gpio.GpioFunction;
import org.air_controller.sensorValues.Humidity;
import org.air_controller.sensorValues.InvalidArgumentException;
import org.air_controller.sensorValues.SensorData;
import org.air_controller.sensorValues.Temperature;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

class Dht22Impl implements Dht22 {
    private static final int MAX_NR_OF_RETRIES = 3;

    private final OneWireCommunication communication;
    private final int retryBaseTime;

    public Dht22Impl() throws IOException {
        this(new OneWireCommunication(GpioFunction.DHT22_SENSOR), 5);
    }

    Dht22Impl(OneWireCommunication communication, int retryBaseTime) {
        this.communication = communication;
        this.retryBaseTime = retryBaseTime;
    }

    @Override
    public Optional<SensorData> refreshData() {
        for (int retryCounter = 0; retryCounter <= MAX_NR_OF_RETRIES; retryCounter++) {
            final OptionalLong rawSensorData = communication.readSensorData();
            if (rawSensorData.isPresent() && checkParity(rawSensorData.getAsLong())) {
                return getSensorDataFromData(rawSensorData.getAsLong());
            }
            sleepABit(retryCounter);
        }
        return Optional.empty();
    }

    private void sleepABit(int retryCounter) {
        final int sleepDurraction = retryBaseTime * retryCounter;
        try {
            TimeUnit.SECONDS.sleep(sleepDurraction);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<SensorData> getSensorDataFromData(long rawSensorData) {
        try {
            final Temperature temperature = getTemperatureFromData(rawSensorData);
            final Humidity humidity = getHumidityFromData(rawSensorData, temperature);
            final SensorData sensorData = new Dht22SensorData(temperature, humidity);
            return Optional.of(sensorData);
        } catch (InvalidArgumentException e) {
            return Optional.empty();
        }
    }

    private Humidity getHumidityFromData(long sensorData, Temperature temperature) throws InvalidArgumentException {
        final long humidityData = (sensorData >> 24) & 0xFFFF;
        final double humidity = (double) humidityData / 10.0;
        return Humidity.createFromRelative(humidity, temperature);
    }

    private Temperature getTemperatureFromData(long sensorData) throws InvalidArgumentException {
        final long temperatureData = (sensorData >> 8) & 0xFFFF;
        final double sign = (temperatureData & 0x8000) == 0 ? 1 : -1;
        final double temperature = (double) (temperatureData & 0x7FFF) / 10.0 * sign;
        return Temperature.createFromCelsius(temperature);
    }

    private boolean checkParity(long sensorData) {
        final long humidity1 = (sensorData >> 32) & 0xFF;
        final long humidity0 = (sensorData >> 24) & 0xFF;
        final long temperature1 = (sensorData >> 16) & 0xFF;
        final long temperature0 = (sensorData >> 8) & 0xFF;
        final long parityByte = sensorData & 0xFF;
        return parityByte == ((humidity1 + humidity0 + temperature1 + temperature0) & 0xFF);
    }
}
