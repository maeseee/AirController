package org.airController.sensor;

import com.pi4j.wiringpi.Gpio;
import org.airController.gpioAdapter.GpioFunction;

import java.io.IOException;
import java.util.Arrays;

class OneWireCommunication {
    private static final int MAX_TIMINGS = 85;

    private final int[] sensorData = {0, 0, 0, 0, 0};
    private final GpioFunction gpioFunction;

    public OneWireCommunication(GpioFunction gpioFunction) throws IOException {
        this.gpioFunction = gpioFunction;
        setupWiringPi();
    }

    public int readSensorData() {
        Arrays.fill(sensorData, 0);
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
                sensorData[pollPosition / 8] <<= 1;
                if (counter > 16) {
                    sensorData[pollPosition / 8] |= 1;
                }
                pollPosition++;
            }
        }
        return pollPosition;
    }

    public int[] getSensorData() {
        return sensorData;
    }

    private void setupWiringPi() throws IOException {
        final int wiringPiStatus = Gpio.wiringPiSetup();
        if (wiringPiStatus == -1) {
            throw new IOException("GPIO SETUP FAILED");
        }
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
}
