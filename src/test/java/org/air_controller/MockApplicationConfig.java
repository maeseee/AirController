package org.air_controller;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.MockGpioPin;
import org.air_controller.persistence.LocalInMemoryDatabase;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemPart;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockApplicationConfig {

    @Bean
    @Primary
    public GpioPins createMockPins() {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final GpioPin humidityExchanger = new MockGpioPin("HUMIDITY_EXCHANGER", true);
        return new GpioPins(airFlow, humidityExchanger);
    }

    @Bean("airFlowAccessor")
    @Primary

    public SystemActionDbAccessor createAirFlowSystemActionDbAccessor() {
        return createSystemActionDbAccessorWithLocalDb(SystemPart.AIR_FLOW);
    }

    @Bean("humidityAccessor")
    @Primary
    public SystemActionDbAccessor createHumiditySystemActionDbAccessor() {
        return createSystemActionDbAccessorWithLocalDb(SystemPart.HUMIDITY);
    }

    private static SystemActionDbAccessor createSystemActionDbAccessorWithLocalDb(SystemPart systemPart) {
        return new SystemActionDbAccessor(new LocalInMemoryDatabase(), systemPart);
    }
}
