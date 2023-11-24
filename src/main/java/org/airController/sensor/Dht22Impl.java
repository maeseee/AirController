package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.sensorAdapter.SensorValue;

import java.io.IOException;
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
    public SensorValue refreshData() {
        AirValue airValue = null;
        int retryCounter = 0;
        while (airValue == null && retryCounter <= MAX_NR_OF_RETRIES) {
            final OptionalLong sensorData = communication.readSensorData();
            if (sensorData.isPresent() && checkParity(sensorData.getAsLong())) {
                airValue = getSensorValueFromData(sensorData.getAsLong());
            } else {
                retryCounter++;
                sleepABit(retryCounter);
            }
        }
        return new SensorValueImpl(airValue);
    }

    private void sleepABit(int retryCounter) {
        final int sleepDurraction = retryBaseTime * retryCounter;
        try {
            TimeUnit.SECONDS.sleep(sleepDurraction);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private AirValue getSensorValueFromData(long sensorData) {
        try {
            final Humidity humidity = getHumidityFromData(sensorData);
            final Temperature temperature = getTemperatureFromData(sensorData);
            return new AirValue(temperature, humidity);
        } catch (IOException e) {
            return null;
        }
    }

    private Humidity getHumidityFromData(long sensorData) throws IOException {
        final long humidityData = (sensorData >> 24) & 0xFFFF;
        final double humidity = (double) humidityData / 10.0;
        return Humidity.createFromRelative(humidity);
    }

    private Temperature getTemperatureFromData(long sensorData) {
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
