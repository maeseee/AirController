package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.sensorAdapter.SensorValue;

import java.io.IOException;
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
        AirVO airVO = null;
        int retryCounter = 0;
        while (airVO == null && retryCounter <= MAX_NR_OF_RETRIES) {
            final int nrOfReadPolls = communication.readSensorData();
            final int[] sensorData = communication.getSensorData();
            if (readSuccessful(nrOfReadPolls, sensorData)) {
                airVO = getSensorValueFromData(sensorData);
            } else {
                retryCounter++;
                sleepABit(retryCounter);
            }
        }
        return new SensorValueImpl(airVO);
    }

    private void sleepABit(int retryCounter) {
        final int sleepDurraction = retryBaseTime * retryCounter;
        try {
            TimeUnit.SECONDS.sleep(sleepDurraction);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean readSuccessful(int nrOfReadPolls, int[] sensorData) {
        return nrOfReadPolls >= 40 && checkParity(sensorData);
    }

    private AirVO getSensorValueFromData(int[] sensorData) {
        try {
            final Humidity humidity = getHumidityFromData(sensorData);
            final Temperature temperature = getTemperatureFromData(sensorData);
            return new AirVO(temperature, humidity);
        } catch (IOException e) {
            return null;
        }
    }

    private Humidity getHumidityFromData(int[] sensorData) throws IOException {
        final double humidity = (double) ((sensorData[0] << 8) + sensorData[1]) / 10;
        return Humidity.createFromRelative(humidity);
    }

    private Temperature getTemperatureFromData(int[] sensorData) {
        final double sign = (sensorData[2] & 0x80) == 0 ? 1 : -1;
        final double temperature = (double) (((sensorData[2] & 0x7F) << 8) + sensorData[3]) / 10.0 * sign;
        return Temperature.createFromCelsius(temperature);
    }

    private boolean checkParity(int[] sensorData) {
        return sensorData[4] == (sensorData[0] + sensorData[1] + sensorData[2] + sensorData[3] & 0xFF);
    }
}
