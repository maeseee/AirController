package org.air_controller;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.MockGpioPin;
import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemDbAccessors;
import org.air_controller.system_action.SystemPart;

class MainMock {

    static void main() throws InterruptedException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final GpioPin humidityExchanger = new MockGpioPin("HUMIDITY_EXCHANGER", true);
        final GpioPins gpioPins = new GpioPins(airFlow, humidityExchanger);
        final VentilationSystemDbAccessors dbAccessors = createSystemActionDbAccessors();
        final ApplicationPersistence persistence = new ApplicationPersistence(dbAccessors);
        final ApplicationBuilder builder = new ApplicationBuilder(gpioPins, persistence);
        final Application application = builder.build();
        application.run();
        Thread.currentThread().join();
    }

    private static VentilationSystemDbAccessors createSystemActionDbAccessors() {
        return new VentilationSystemDbAccessors(
                createSystemActionDbAccessorWithLocalDb(SystemPart.AIR_FLOW),
                createSystemActionDbAccessorWithLocalDb(SystemPart.HUMIDITY));
    }

    private static SystemActionDbAccessor createSystemActionDbAccessorWithLocalDb(SystemPart systemPart) {
        return new SystemActionDbAccessor(new LocalInMemoryDatabase(), systemPart);
    }
}