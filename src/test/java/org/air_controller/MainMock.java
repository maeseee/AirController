package org.air_controller;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.MockGpioPin;
import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemPart;

import java.sql.SQLException;

class MainMock {

    public static void main(String[] args) throws InterruptedException, SQLException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final GpioPin humidityExchanger = new MockGpioPin("HUMIDITY_EXCHANGER", true);
        final GpioPins gpioPins = new GpioPins(airFlow, humidityExchanger);
        final ApplicationBuilder builder = new ApplicationBuilder();
        final ApplicationBuilderSharedObjects sharedObjects = new ApplicationBuilderSharedObjects();
        sharedObjects.setGpioPins(gpioPins);
        sharedObjects.setSystemActionDbAccessors(createSystemActionDbAccessors());
        builder.setSharedObjects(sharedObjects);
        final Application application = builder.build();
        application.run();
        Thread.currentThread().join();
    }

    private static SystemActionDbAccessors createSystemActionDbAccessors() throws SQLException {
        return new SystemActionDbAccessors(
                createSystemActionDbAccessorWithLocalDb(SystemPart.AIR_FLOW),
                createSystemActionDbAccessorWithLocalDb(SystemPart.HUMIDITY));
    }

    private static SystemActionDbAccessor createSystemActionDbAccessorWithLocalDb(SystemPart systemPart) throws SQLException {
        return new SystemActionDbAccessor(new LocalInMemoryDatabase(), systemPart);
    }
}