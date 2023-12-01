package org.airController.sensor;

import org.airController.gpioAdapter.GpioFunction;
import org.airController.util.Logging;
import org.airController.util.RaspberryPiPin;

import java.io.IOException;
import java.util.OptionalLong;

class OneWireCommunication {
    private static final int MAX_TIMINGS = 83;
    private static final int NR_OF_BITS = 40;
    private static final int MIN_HIGH_DURATION = 16;

    private final RaspberryPiPin raspberryPiPin;

    public OneWireCommunication(GpioFunction gpioFunction) throws IOException {
        this(new RaspberryPiPin(gpioFunction));
    }

    OneWireCommunication(RaspberryPiPin raspberryPiPin) {
        this.raspberryPiPin = raspberryPiPin;
    }

    public OptionalLong readSensorData() {
        sendStartSignal();
        raspberryPiPin.setMode(true);
        boolean lastState = true;
        int bitPosition = 0;
        long sensorData = 0;
        for (int transition = 0; transition < MAX_TIMINGS; transition++) {
            final int microsecondsToStateChange = waitUntilStateChanges(lastState);
            lastState = raspberryPiPin.read();
            if (microsecondsToStateChange == 255) {
                Logging.getLogger().severe("OneWireCommunication timeout. transition=" + transition);
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

    private void sendStartSignal() {
        raspberryPiPin.setMode(false);
        raspberryPiPin.write(false);
        raspberryPiPin.sleep(18);
        raspberryPiPin.setMode(true);
    }

    private int waitUntilStateChanges(boolean lastState) {
        int microsecondCounter = 0;
        while (raspberryPiPin.read() == lastState && microsecondCounter < 255) {
            microsecondCounter++;
            raspberryPiPin.sleep(1);
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
