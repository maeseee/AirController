package org.airController.sensor;

import com.pi4j.wiringpi.Gpio;
import org.airController.entities.AirVO;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.sensorAdapter.SensorValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

class Dht22Impl implements Dht22 {
    private static final int MAX_TIMINGS = 85;
    private static final int MAX_NR_OF_RETRIES = 3;

    private final int[] dht22_dat = {0, 0, 0, 0, 0};
    private final GpioFunction gpioFunction;

    public Dht22Impl(GpioFunction gpioFunction) throws IOException {
        this.gpioFunction = gpioFunction;
        setupWiringPi();
    }

    private void setupWiringPi() throws IOException {
        final int wiringPiStatus = Gpio.wiringPiSetup();
        if (wiringPiStatus == -1) {
            throw new IOException("GPIO SETUP FAILED");
        }
    }

    @Override
    public SensorValue refreshData() {
        AirVO airVO = null;
        int retryCounter = 0;
        while (airVO == null && retryCounter < MAX_NR_OF_RETRIES) {
            final int nrOfReadPolls = readSensorData();
            if (readSuccessful(nrOfReadPolls)) {
                airVO = getSensorValueFromData();
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

    private boolean readSuccessful(int nrOfReadPolls) {
        return nrOfReadPolls >= 40 && checkParity();
    }

    private AirVO getSensorValueFromData() {
        final float humidity = getHumidityFromData();
        final float temperature = getTemperatureFromData();
        return new AirVO(Temperature.createFromCelsius(temperature), Humidity.createFromRelative(humidity));
    }

    private void sendStartSignal() {
        Gpio.pinMode(gpioFunction.getGpio(), Gpio.OUTPUT);
        Gpio.digitalWrite(gpioFunction.getGpio(), Gpio.LOW);
        Gpio.delay(18);
        Gpio.digitalWrite(gpioFunction.getGpio(), Gpio.HIGH);
    }

    private int waitUntilStateChanges(int lastState) {
        int counter = 0;
        while (Gpio.digitalRead(gpioFunction.getGpio()) == lastState && counter < 255) {
            counter++;
            Gpio.delayMicroseconds(1);
        }
        return counter;
    }

    private int readSensorData() {
        Arrays.fill(dht22_dat, 0);
        sendStartSignal();
        Gpio.pinMode(gpioFunction.getGpio(), Gpio.INPUT);
        int lastState = Gpio.HIGH;
        int pollPosition = 0;
        for (int i = 0; i < MAX_TIMINGS; i++) {
            final int counter = waitUntilStateChanges(lastState);

            lastState = Gpio.digitalRead(gpioFunction.getGpio());

            if (counter == 255) {
                break;
            }

            /* ignore first 3 transitions */
            if (i >= 4 && i % 2 == 0) {
                /* shove each bit into the storage bytes */
                dht22_dat[pollPosition / 8] <<= 1;
                if (counter > 16) {
                    dht22_dat[pollPosition / 8] |= 1;
                }
                pollPosition++;
            }
        }
        return pollPosition;
    }

    private float getHumidityFromData() {
        return (float) ((dht22_dat[0] << 8) + dht22_dat[1]) / 10;
    }

    private float getTemperatureFromData() {
        return (float) (((dht22_dat[2] & 0x7F) << 8) + dht22_dat[3]) / 10;
    }

    private boolean checkParity() {
        return dht22_dat[4] == (dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3] & 0xFF);
    }
}
