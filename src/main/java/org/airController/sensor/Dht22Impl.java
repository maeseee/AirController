package org.airController.sensor;

import com.pi4j.wiringpi.Gpio;
import org.airController.entities.AirVO;
import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.gpioAdapter.GpioFunction;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

class Dht22Impl implements Dht22 {
    private static final int MAX_TIMINGS = 85;
    private final int[] dht22_dat = {0, 0, 0, 0, 0};
    private final GpioFunction gpioFunction;

    public Dht22Impl(GpioFunction gpioFunction) throws IOException {
        this.gpioFunction = gpioFunction;

        if (Gpio.wiringPiSetup() == -1) {
            throw new IOException("GPIO SETUP FAILED");
        }
    }

    @Override
    public Optional<AirVO> refreshData() {
        final int pollDataCheck = pollDHT22();
        if (pollDataCheck < 40 || !checkParity()) {
            return Optional.empty();
        }

        final float humidity = getHumidityFromData();
        final float temperature = getTemperatureFromData();

        final AirVO airVO = new AirVO(Temperature.createFromCelsius(temperature), Humidity.createFromRelative(humidity));
        return Optional.of(airVO);
    }

    private int pollDHT22() {
        int lastState = Gpio.HIGH;
        Arrays.fill(dht22_dat, 0);

        sendStartSignal();

        Gpio.pinMode(gpioFunction.getGpio(), Gpio.INPUT);

        return readSensorData(lastState);
    }

    private void sendStartSignal() {
        Gpio.pinMode(gpioFunction.getGpio(), Gpio.OUTPUT);
        Gpio.digitalWrite(gpioFunction.getGpio(), Gpio.LOW);
        Gpio.delay(18);
        Gpio.digitalWrite(gpioFunction.getGpio(), Gpio.HIGH);
    }

    private int waitUntilStateChanges(int lastState) {
        int counter = 0;
        while (Gpio.digitalRead(gpioFunction.getGpio()) == lastState) {
            counter++;
            Gpio.delayMicroseconds(1);
            if (counter == 255) {
                break;
            }
        }
        return counter;
    }

    private int readSensorData(int lastState) {
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
