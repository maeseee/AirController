package org.air_controller;

import lombok.Getter;
import org.air_controller.gpio.GpioPins;
import org.air_controller.rules.FreshAirRuleBuilder;
import org.air_controller.rules.Rule;
import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemActionPersistence;

import java.util.List;

class ApplicationBuilderSharedObjects {
    private final GpioPins gpioPins;
    @Getter
    private final SystemActionDbAccessors systemActionDbAccessors;
    @Getter
    private final List<VentilationSystem> ventilationSystems;
    private List<Rule> freshAirRules;

    public ApplicationBuilderSharedObjects(GpioPins gpioPins, SystemActionDbAccessors systemActionDbAccessors) {
        this.gpioPins = gpioPins;
        this.systemActionDbAccessors = systemActionDbAccessors;
        this.ventilationSystems = createVentilationSystems();
    }

    public List<Rule> getOrCreateFreshAirRules(ClimateSensors sensors) {
        if (freshAirRules == null) {
            createFreshAirRules(sensors, systemActionDbAccessors.airFlow());
        }
        return freshAirRules;
    }

    private List<VentilationSystem> createVentilationSystems() {
        final VentilationSystem ventilationSystem = new ControlledVentilationSystem(gpioPins);
        final SystemActionPersistence systemActionPersistence = new SystemActionPersistence(getSystemActionDbAccessors());
        return List.of(ventilationSystem, systemActionPersistence);
    }

    private void createFreshAirRules(ClimateSensors sensors, SystemActionDbAccessor dbAccessor) {
        freshAirRules = new FreshAirRuleBuilder().build(sensors, dbAccessor);
    }
}
