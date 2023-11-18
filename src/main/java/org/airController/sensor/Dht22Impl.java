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

    public Dht22Impl() throws IOException {
        this(new OneWireCommunication(GpioFunction.DHT22_SENSOR));
    }

    Dht22Impl(OneWireCommunication communication) {
        this.communication = communication;
    }

    @Override
    public SensorValue refreshData() {
        AirVO airVO = null;
        int retryCounter = 0;
        while (airVO == null && retryCounter < MAX_NR_OF_RETRIES) {
            final int nrOfReadPolls = communication.readSensorData();
            final int[] sensorData = communication.getSensorData();
            if (readSuccessful(nrOfReadPolls, sensorData)) {
                airVO = getSensorValueFromData(sensorData);
            } else {
                sleepAFew();
            }
            retryCounter++;
        }
        return new SensorValueImpl(airVO);
    }

    private void sleepAFew() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean readSuccessful(int nrOfReadPolls, int[] sensorData) {
        return nrOfReadPolls >= 40 && checkParity(sensorData);
    }

    private AirVO getSensorValueFromData(int[] sensorData) {
        final float humidity = getHumidityFromData(sensorData);
        final float temperature = getTemperatureFromData(sensorData);
        return new AirVO(Temperature.createFromCelsius(temperature), Humidity.createFromRelative(humidity));
    }

    private float getHumidityFromData(int[] sensorData) {
        return (float) ((sensorData[0] << 8) + sensorData[1]) / 10;
    }

    private float getTemperatureFromData(int[] sensorData) {
        return (float) (((sensorData[2] & 0x7F) << 8) + sensorData[3]) / 10;
    }

    private boolean checkParity(int[] sensorData) {
        return sensorData[4] == (sensorData[0] + sensorData[1] + sensorData[2] + sensorData[3] & 0xFF);
    }
}
