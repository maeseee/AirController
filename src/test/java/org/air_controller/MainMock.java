package org.air_controller;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.MockGpioPin;

import java.sql.SQLException;

class MainMock {

    public static void main(String[] args) throws InterruptedException, SQLException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final GpioPin humidityExchanger = new MockGpioPin("HUMIDITY_EXCHANGER", true);
        final Application application = new Application(airFlow, humidityExchanger);
        application.run();
        Thread.currentThread().join();
    }
}