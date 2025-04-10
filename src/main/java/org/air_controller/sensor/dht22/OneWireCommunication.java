package org.air_controller.sensor.dht22;

import com.pi4j.wiringpi.Gpio;
import org.air_controller.gpio.raspberry.GpioFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.OptionalLong;

class OneWireCommunication {
    private static final Logger logger = LogManager.getLogger(OneWireCommunication.class);
    private static final int MAX_TIMINGS = 83;
    private static final int NR_OF_BITS = 40;
    private static final int MIN_HIGH_DURATION = 16;

    private final GpioFunction gpioFunction;

    public OneWireCommunication(GpioFunction gpioFunction) throws IOException {
        this.gpioFunction = gpioFunction;
        setupWiringPi();
    }

    public OptionalLong readSensorData() {
        sendStartSignal();
        Gpio.pinMode(gpioFunction.getGpio(), Gpio.INPUT);
        int lastState = Gpio.HIGH;
        int bitPosition = 0;
        long sensorData = 0;
        for (int transition = 0; transition < MAX_TIMINGS; transition++) {
            final int microsecondsToStateChange = waitUntilStateChanges(lastState);
            lastState = Gpio.digitalRead(gpioFunction.getGpio());
            if (microsecondsToStateChange == 255) {
                logger.error("OneWireCommunication timeout. transition={}", transition);
                break;
            }

            if (isBitReadState(transition)) {
                sensorData = setBit(isBitHigh(microsecondsToStateChange), sensorData, bitPosition);
                bitPosition++;
            }
        }

        if (bitPosition != 40) {
            return OptionalLong.empty();
        }

        return OptionalLong.of(sensorData);
    }

    private long setBit(boolean bitHigh, long sensorData, int bitPosition) {
        if (bitHigh) {
            sensorData |= (1L << (NR_OF_BITS - bitPosition - 1));
        }
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
        int microsecondCounter = 0;
        while (Gpio.digitalRead(gpioFunction.getGpio()) == lastState && microsecondCounter < 255) {
            microsecondCounter++;
            Gpio.delayMicroseconds(1);
        }
        return microsecondCounter;
    }

    private boolean isBitReadState(int transition) {
        // ignore first 3 transitions
        return transition >= 4 && transition % 2 == 0;
    }

    private boolean isBitHigh(long microsecondsToStateChange) {
        return microsecondsToStateChange > MIN_HIGH_DURATION;
    }
}
